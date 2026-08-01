import admin from "firebase-admin";
import fs from "node:fs";
import path from "node:path";

const serviceAccountPath = process.env.FIREBASE_SERVICE_ACCOUNT;
if (!serviceAccountPath) {
  console.error("Missing FIREBASE_SERVICE_ACCOUNT environment variable.");
  process.exit(1);
}

const referencia = process.argv.slice(2).join(" ").trim();
if (!referencia) {
  console.error('Usage: npm run add-temporada -- "Julio 2026"');
  process.exit(1);
}

const serviceAccount = JSON.parse(
  fs.readFileSync(path.resolve(serviceAccountPath), "utf8")
);

admin.initializeApp({
  credential: admin.credential.cert(serviceAccount),
  databaseURL: "https://distribuidora-carols-default-rtdb.firebaseio.com"
});

const db = admin.database();

async function run() {
  const ref = db.ref("CATALOGO/Listado Temporadas").push();

  await ref.set({
    referencia
  });

  console.log(`Created temporada node: ${ref.key}`);
  console.log(`referencia: "${referencia}"`);
}

run().catch((error) => {
  console.error(error);
  process.exit(1);
});
