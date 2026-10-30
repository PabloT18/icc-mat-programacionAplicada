import fs from 'node:fs/promises';
import path from 'node:path';
import { fileURLToPath } from 'node:url';
import { marked } from './vendor/marked.mjs';

const root = path.resolve(path.dirname(fileURLToPath(import.meta.url)), '..');
const data = JSON.parse(await fs.readFile(path.join(root, 'assets/catalogo.json'), 'utf8'));
const esc = s => String(s).replace(/[&<>"']/g, c => ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;' }[c]));
const relative = (base, target) =>
  path.relative(
    path.join(root, base),
    path.join(root, target)
  ).split(path.sep).join('/');

async function write(p, s) { await fs.mkdir(path.dirname(path.join(root, p)), { recursive: true }); await fs.writeFile(path.join(root, p), s); }
// base: carpeta donde se generará el HTML, relativa a la raíz.
const head = (title, base = '') => `
<!doctype html>
<html lang="es">
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width,initial-scale=1">

  <title>${esc(title)} · Programación Aplicada</title>

  <link
    rel="stylesheet"
    href="${relative(base, 'assets/css/styles.css')}"
  >

  <script
    defer
    src="${relative(base, 'assets/js/vendor/mermaid.min.js')}"
  ></script>

  <script
    defer
    src="${relative(base, 'assets/js/mermaid-init.js')}"
  ></script>
  <script
    defer
    src="${relative(base, 'assets/js/diagram-viewer.js')}"
  ></script>
</head>`;
const wrapTables = s => s.replace(/<table>/g, '<div class="table-scroll"><table>').replace(/<\/table>/g, '</table></div>');
for (const t of data.topics) {
  const md = await fs.readFile(path.join(root, t.path, 'material.md'), 'utf8');

  let body = marked.parse(md, { gfm: true });

  // Identificar imágenes SVG y aplicar tamaños.
  body = body.replace(
    /<img\b[^>]*>/gi,
    (img) => {
      const src = img.match(/\bsrc=["']([^"']+)["']/i);

      if (!src || !/\.svg(?:[?#].*)?$/i.test(src[1])) {
        return img;
      }

      const alt = img.match(/\balt=["']([^"']*)["']/i);

      let size = 'medium';
      let cleanAlt = alt ? alt[1] : '';

      const sizeMatch = cleanAlt.match(
        /\s*\|\s*(small|medium|large)\s*$/i
      );

      if (sizeMatch) {
        size = sizeMatch[1].toLowerCase();
        cleanAlt = cleanAlt.slice(0, sizeMatch.index).trim();
      }

      // Eliminar el tamaño del texto alternativo.
      if (alt) {
        img = img.replace(
          /\balt=(["'])[^"']*\1/i,
          `alt="${cleanAlt}"`
        );
      }

      // Agregar clases CSS.
      const classes = `zoomable-diagram ${size}`;

      if (/\bclass=["'][^"']*["']/i.test(img)) {
        img = img.replace(
          /\bclass=(["'])(.*?)\1/i,
          (_, quote, existing) =>
            `class=${quote}${existing} ${classes}${quote}`
        );
      } else {
        img = img.replace(/>$/, ` class="${classes}">`);
      }

      return img;
    }
  );

  // Keep the Markdown source available, while navigating related lessons as HTML.

  body = body.replace(
    /href="([^"]*\/material)\.md(#[^"]*)?"/g,
    (_, base, hash) =>
      `href="${base.slice(0, -8)}index.html${hash || ''}"`
  );
  const nav = `
  <nav class="article-nav" aria-label="Recursos del contenido">
    <a href="${relative(t.path, 'index.html')}" target="_top">Índice del curso</a>
    <a href="material.md" download>Descargar MD</a>
    <a href="presentacion.pptx" download>Descargar PPTX</a>
    ${t.prev ? `<a href="${relative(t.path, t.prev + '/index.html')}">Anterior</a>` : ''}
    ${t.next ? `<a href="${relative(t.path, t.next + '/index.html')}">Siguiente</a>` : ''}
  </nav>`;
  await write(
    t.path + '/index.html',
    head(t.id + ' · ' + t.title, t.path) + `
  <body>
    <a class="skip" href="#contenido">Ir al material</a>

    <main class="page" id="contenido">
      ${nav}
      ${wrapTables(body)}
      ${nav}
    </main>
  </body>
  </html>`
  );
}

const navData = { topics: data.topics.map(({ id, title, path, unit }) => ({ id, title, path, unit })), labs: data.labs.map(({ id, title, unit, problem }) => ({ id, title, unit, problem })), units: data.units };
await write('assets/js/course-data.js', 'window.COURSE = ' + JSON.stringify(navData).replace(/</g, '\\u003c') + ';');
await write('index.html', head('Material de curso', '') + `<body><a class="skip" href="#main">Ir al contenido</a><header class="topbar"><img class="brand" src="assets/logos/ups-computacion.png" alt="UPS · Carrera de Computación · Sede Cuenca"><span class="course-label">Programación Aplicada</span></header><div class="shell"><aside class="sidebar"><h2>Unidades del curso</h2><nav class="unit-nav" aria-label="Unidades">${Object.entries(data.units).map(([u, [name]]) => `<button class="unit-button" data-unit="${u}"><span>Unidad ${u}</span><small>${esc(name)}</small></button>`).join('')}</nav><div class="support"><p>Proyecto acumulativo<br><strong>CampusMonitor</strong></p><a href="README.md">README del proyecto</a></div></aside><main class="workspace" id="main"><section id="listing"><p class="eyebrow" id="unitLabel"></p><h1 id="unitTitle"></h1><p class="intro">Consulta el material, ejecuta la demostración y resuelve la PC en tu proyecto Java.</p><div class="tabs" aria-label="Tipo de recurso"><button class="tab active" data-tab="material" aria-pressed="true">Material de clase</button><button class="tab" data-tab="labs" aria-pressed="false">Prácticas de laboratorio</button></div><div id="resources"></div><p class="footer">Ing. Pablo Torres, Mgtr. · Universidad Politécnica Salesiana</p></section><section id="reader" class="hidden"><div class="viewer-bar"><button class="button" id="back">Volver a la unidad</button><div class="viewer-title" id="readerTitle"></div><div class="viewer-links"><a id="fullPage" class="button" target="_blank" rel="noopener">Abrir página</a><a id="deckLink" class="button primary" download>PPTX</a></div></div><iframe class="viewer" id="viewer" title="Material de Programación Aplicada"></iframe></section></main></div><script src="assets/js/course-data.js"></script><script src="assets/js/app.js"></script></body></html>`);
console.log(`HTML generado: ${data.topics.length} contenidos y ${data.labs.length} prácticas.`);
