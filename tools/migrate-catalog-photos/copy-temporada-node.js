import admin from "firebase-admin";
import fs from "node:fs";
import path from "node:path";

const serviceAccountPath = process.env.FIREBASE_SERVICE_ACCOUNT;
if (!serviceAccountPath) {
  console.error("Missing FIREBASE_SERVICE_ACCOUNT environment variable.");
  process.exit(1);
}

const [sourceNode, targetSeason] = process.argv.slice(2);
if (!sourceNode || !targetSeason) {
  console.error('Usage: npm run copy-temporada-node -- "Julio" "Julio 2026"');
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
  const sourceRef = db.ref(`CATALOGO/${sourceNode}`);
  const targetRef = db.ref(`CATALOGO/Temporadas/${targetSeason}`);

  const snapshot = await sourceRef.get();
  if (!snapshot.exists()) {
    throw new Error(`Source node does not exist: CATALOGO/${sourceNode}`);
  }

  const data = snapshot.val();
  const itemCount = Object.keys(data || {}).length;

  await targetRef.update(data);

  console.log(`Copied ${itemCount} item(s)`);
  console.log(`from: CATALOGO/${sourceNode}`);
  console.log(`to:   CATALOGO/Temporadas/${targetSeason}`);
}

run().catch((error) => {
  console.error(error);
  process.exit(1);
});
