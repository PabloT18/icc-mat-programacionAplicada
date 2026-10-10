import fs from 'node:fs/promises';
import path from 'node:path';
import { fileURLToPath } from 'node:url';

// Ejecutar sin argumentos permite revisar el cambio antes de escribirlo.
const root = path.resolve(path.dirname(fileURLToPath(import.meta.url)), '..');
const archivo = path.join(root, 'assets/catalogo.json');
const original = await fs.readFile(archivo, 'utf8');
const data = JSON.parse(original);
if (!Array.isArray(data.topics)) throw new Error('El catálogo debe contener topics.');
const base = '01/1.4-patrones-genericos';
const indices = data.topics.flatMap((t, i) =>
  t.path === base || t.path?.startsWith(base + '/') ? [i] : []);
if (![1, 3].includes(indices.length) || indices.some((v, i) => v !== indices[0] + i)) {
  throw new Error('Se esperaba el tema 1.4 original o sus tres subtemas consecutivos.');
}
const inicio = indices[0];
const cantidad = indices.length;
const unidad = data.topics[inicio].unit;
const previo = data.topics[inicio - 1];
const siguiente = data.topics[inicio + cantidad];
const entradas = [
  ['1.4a', 'Patrones creacionales', '01-creacionales'],
  ['1.4b', 'Patrones estructurales', '02-estructurales'],
  ['1.4c', 'Patrones de comportamiento', '03-comportamiento']
].map(([id, title, carpeta]) => ({ id, title, unit: unidad, path: `${base}/${carpeta}` }));
for (let i = 0; i < entradas.length; i++) {
  const t = entradas[i];
  t.prev = i ? entradas[i - 1].path : (previo?.path ?? null);
  t.next = i < entradas.length - 1 ? entradas[i + 1].path : (siguiente?.path ?? null);
  await fs.access(path.join(root, t.path, 'material.md'));
}
console.log(JSON.stringify(entradas, null, 2));
if (process.argv.includes('--aplicar')) {
  const respaldo = archivo + '.bak-' + Date.now();
  await fs.copyFile(archivo, respaldo, fs.constants.COPYFILE_EXCL);
  data.topics.splice(inicio, cantidad, ...entradas);
  if (previo) previo.next = entradas[0].path;
  if (siguiente) siguiente.prev = entradas.at(-1).path;
  await fs.writeFile(archivo, JSON.stringify(data, null, 2) + '\n');
  console.log('Catálogo actualizado. Respaldo: ' + path.basename(respaldo));
} else {
  console.log('Revisión sin cambios. Añade --aplicar para guardar con respaldo.');
}
