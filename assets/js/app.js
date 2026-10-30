(() => {
  const data = window.COURSE;
  let unit = 1, tab = 'material';
  const $ = id => document.getElementById(id);
  const escape = s => String(s).replace(/[&<>"']/g, c => ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;' }[c]));
  function render() {
    $('listing').classList.remove('hidden'); $('reader').classList.add('hidden');
    $('unitLabel').textContent = `UNIDAD ${String(unit).padStart(2, '0')}`;
    $('unitTitle').textContent = data.units[unit][0];
    document.querySelectorAll('[data-unit]').forEach(b => { const active = Number(b.dataset.unit) === unit; b.classList.toggle('active', active); b.setAttribute('aria-current', active ? 'true' : 'false'); });
    document.querySelectorAll('[data-tab]').forEach(b => { const active = b.dataset.tab === tab; b.classList.toggle('active', active); b.setAttribute('aria-pressed', String(active)); });
    if (tab === 'material') {
      $('resources').innerHTML = '<ul class="topic-list">' + data.topics.filter(t => t.unit === unit).map(t => `<li class="topic-row"><span class="topic-code">${escape(t.id)}</span><a class="topic-name" href="${t.path}/index.html" data-open="${t.id}">${escape(t.title)}</a><div class="topic-actions"><a class="open" href="${t.path}/index.html" data-open="${t.id}">Leer</a><a href="${t.path}/material.md" download>MD</a><a href="${t.path}/presentacion.pptx" download>PPTX</a></div></li>`).join('') + '</ul>';
    } else {
      $('resources').innerHTML = data.labs
        .filter(l => l.unit === unit)
        .map(l => `
      <article class="lab-card">
        <p class="eyebrow">${escape(l.id)}</p>
        <h3>${escape(l.title)}</h3>
        <p>${escape(l.problem)}</p>
        <div class="topic-actions">
          <a class="open"
             data-lab="${escape(l.id)}"
             href="${String(unit).padStart(2, '0')}/PLs/${l.id}.html">
            Abrir guía
          </a>
        </div>
      </article>
    `).join('');
    }
  }
  function openResource(id, lab = false) {
    const item = (lab ? data.labs : data.topics).find(t => t.id === id); if (!item) return;
    unit = item.unit;
    const base = lab ? `${String(unit).padStart(2, '0')}/PLs/${item.id}` : item.path;
    const page = lab ? base + '.html' : base + '/index.html';
    $('listing').classList.add('hidden'); $('reader').classList.remove('hidden');
    $('readerTitle').textContent = item.id + ' · ' + item.title;
    $('viewer').src = page; $('fullPage').href = page;
    $('deckLink').href = lab ? base + '.pptx' : base + '/presentacion.pptx';
    $('back').focus();
  }
  document.addEventListener('click', e => {
    const unitButton = e.target.closest('[data-unit]'); if (unitButton) { unit = Number(unitButton.dataset.unit); render(); return; }
    const tabButton = e.target.closest('[data-tab]'); if (tabButton) { tab = tabButton.dataset.tab; render(); return; }
    const link = e.target.closest('[data-open],[data-lab]'); if (link) { e.preventDefault(); openResource(link.dataset.open || link.dataset.lab, Boolean(link.dataset.lab)); }
  });
  $('back').addEventListener('click', () => { render(); $('unitTitle').setAttribute('tabindex', '-1'); $('unitTitle').focus(); });
  render();
})();
