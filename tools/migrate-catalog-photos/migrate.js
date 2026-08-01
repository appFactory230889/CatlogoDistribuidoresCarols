import admin from "firebase-admin";
import fs from "node:fs";
import path from "node:path";

const serviceAccountPath = process.env.FIREBASE_SERVICE_ACCOUNT;
if (!serviceAccountPath) {
  console.error("Missing FIREBASE_SERVICE_ACCOUNT environment variable.");
  process.exit(1);
}

const serviceAccount = JSON.parse(
  fs.readFileSync(path.resolve(serviceAccountPath), "utf8")
);

admin.initializeApp({
  credential: admin.credential.cert(serviceAccount),
  databaseURL: "https://distribuidora-carols-default-rtdb.firebaseio.com",
  storageBucket: "distribuidora-carols.firebasestorage.app"
});

const db = admin.database();
const bucket = admin.storage().bucket();

function normalizeCategory(category) {
  return String(category || "Sin categoria")
    .trim()
    .replace(/[.#$/[\]]/g, "")
    .replace(/\s+/g, " ");
}

function buildDestinationPath(item) {
  const category = normalizeCategory(item.categoria);
  const code = String(item.codigo || item.code || "").trim();
  return `Fotos de Catalogo/${category}/${code}.jpg`;
}

function buildPublicUrl(destination) {
  return `https://storage.googleapis.com/${bucket.name}/${encodeURIComponent(destination).replace(/%2F/g, "/")}`;
}

async function downloadBytes(url) {
  const response = await fetch(url);
  if (!response.ok) {
    throw new Error(`Download failed with status ${response.status}`);
  }
  return Buffer.from(await response.arrayBuffer());
}

async function uploadCopy(item, dryRun = false) {
  if (!item?.foto || !item?.codigo) {
    return { skipped: true, reason: "missing foto or codigo" };
  }

  const destination = buildDestinationPath(item);
  const file = bucket.file(destination);
  const publicUrl = buildPublicUrl(destination);

  const [exists] = await file.exists();
  if (exists) {
    return { skipped: true, reason: "already exists", destination, publicUrl };
  }

  if (dryRun) {
    return { copied: false, dryRun: true, destination, publicUrl };
  }

  const bytes = await downloadBytes(item.foto);
  await file.save(bytes, {
    metadata: {
      contentType: "image/jpeg",
      metadata: {
        codigo: String(item.codigo),
        categoria: String(item.categoria || "")
      }
    }
  });

  await file.makePublic().catch(() => {});

  return { copied: true, destination, publicUrl };
}

async function updateItemUrls(item, newUrl) {
  const codigo = String(item?.codigo || "").trim();
  if (!codigo) return;

  const catalogoSnapshot = await db.ref("CATALOGO").get();
  if (!catalogoSnapshot.exists()) return;

  const catalogo = catalogoSnapshot.val() || {};
  const updates = {};

  const rewrittenFields = {
    fotoOriginal: item.foto || "",
    fotoCatalogo: newUrl,
    foto: newUrl
  };

  updates[`CATALOGO/Todas las prendas/${codigo}/fotoOriginal`] = rewrittenFields.fotoOriginal;
  updates[`CATALOGO/Todas las prendas/${codigo}/fotoCatalogo`] = rewrittenFields.fotoCatalogo;
  updates[`CATALOGO/Todas las prendas/${codigo}/foto`] = rewrittenFields.foto;

  for (const [nodeName, nodeValue] of Object.entries(catalogo)) {
    if (nodeName === "Codigo" || !nodeValue || typeof nodeValue !== "object") continue;
    if (!Object.prototype.hasOwnProperty.call(nodeValue, codigo)) continue;

    updates[`CATALOGO/${nodeName}/${codigo}/fotoOriginal`] = rewrittenFields.fotoOriginal;
    updates[`CATALOGO/${nodeName}/${codigo}/fotoCatalogo`] = rewrittenFields.fotoCatalogo;
    updates[`CATALOGO/${nodeName}/${codigo}/foto`] = rewrittenFields.foto;
  }

  await db.ref().update(updates);
}

async function run() {
  const dryRun = process.argv.includes("--dry-run");
  const updateDatabase = process.argv.includes("--update-db");

  const snapshot = await db.ref("CATALOGO/Todas las prendas").get();
  if (!snapshot.exists()) {
    throw new Error("No data found in CATALOGO/Todas las prendas");
  }

  const items = Object.values(snapshot.val() || {});
  let copied = 0;
  let skipped = 0;
  let failed = 0;

  for (const item of items) {
    try {
      const result = await uploadCopy(item, dryRun);
      if (result.skipped) {
        skipped += 1;
        console.log(`SKIP ${item.codigo}: ${result.reason}`);

        if (updateDatabase && result.publicUrl && !dryRun) {
          await updateItemUrls(item, result.publicUrl);
          console.log(`UPDATE ${item.codigo}: foto -> ${result.publicUrl}`);
        }
        continue;
      }

      if (result.dryRun) {
        console.log(`PLAN ${item.codigo}: ${result.destination}`);
        continue;
      }

      copied += 1;
      console.log(`COPY ${item.codigo}: ${result.destination}`);

      if (updateDatabase && result.publicUrl) {
        await updateItemUrls(item, result.publicUrl);
        console.log(`UPDATE ${item.codigo}: foto -> ${result.publicUrl}`);
      }
    } catch (error) {
      failed += 1;
      console.error(`FAIL ${item?.codigo || "sin-codigo"}: ${error.message}`);
    }
  }

  console.log("");
  console.log(`Done. copied=${copied} skipped=${skipped} failed=${failed} dryRun=${dryRun}`);
}

run().catch((error) => {
  console.error(error);
  process.exit(1);
});
