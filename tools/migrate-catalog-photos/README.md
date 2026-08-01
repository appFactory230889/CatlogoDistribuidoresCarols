# Migracion de fotos de catalogo

Este script copia las fotos actuales del catalogo a una nueva ruta en Firebase Storage:

- `Fotos de Catalogo/<categoria>/<codigo>.jpg`

No elimina las fotos originales.

## Que hace

1. Lee `CATALOGO/Todas las prendas` desde Realtime Database.
2. Toma el campo `foto` de cada item.
3. Descarga la imagen desde su URL actual.
4. Sube una copia a `Fotos de Catalogo/<categoria>/<codigo>.jpg`.
5. Opcionalmente guarda la nueva URL en `fotoCatalogo`.

## Requisitos

1. Crear una service account desde Firebase / Google Cloud.
2. Descargar el JSON de credenciales.
3. Tener Node.js instalado.

## Instalar

```bash
cd tools/migrate-catalog-photos
npm install
```

## Simular primero

```bash
$env:FIREBASE_SERVICE_ACCOUNT="C:\ruta\service-account.json"
npm run migrate -- --dry-run
```

## Ejecutar la copia real

```bash
$env:FIREBASE_SERVICE_ACCOUNT="C:\ruta\service-account.json"
npm run migrate
```

## Agregar una temporada al listado

```bash
$env:FIREBASE_SERVICE_ACCOUNT="C:\ruta\service-account.json"
npm run add-temporada -- "Julio 2026"
```

Esto crea un nodo nuevo en:

- `CATALOGO/Listado Temporadas/<auto-id>/referencia`

## Copiar un nodo completo a Temporadas

```bash
$env:FIREBASE_SERVICE_ACCOUNT="C:\ruta\service-account.json"
npm run copy-temporada-node -- "Julio" "Julio 2026"
```

Esto copia todo lo que exista en:

- `CATALOGO/Julio`

hacia:

- `CATALOGO/Temporadas/Julio 2026`

No borra ni mueve el nodo original.

## Ejecutar y reescribir la URL en la base de datos

```bash
$env:FIREBASE_SERVICE_ACCOUNT="C:\ruta\service-account.json"
npm run migrate -- --update-db
```

Con `--update-db`, el script:

- conserva la URL vieja en `fotoOriginal`
- guarda la nueva URL en `fotoCatalogo`
- reemplaza `foto` por la nueva URL
- actualiza `Todas las prendas` y cualquier nodo duplicado del mismo codigo dentro de `CATALOGO`

Esto tambien funciona si las fotos ya fueron copiadas antes: no necesita volver a duplicarlas para reescribir las URLs.

## Nota importante

En Firebase Storage no se crean carpetas vacias. La carpeta `Fotos de Catalogo` y sus subcarpetas aparecen automaticamente cuando el script sube los archivos con esa ruta.
