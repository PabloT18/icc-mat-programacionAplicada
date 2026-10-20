
(() => {
    'use strict';

    let scale = 1;
    let x = 0;
    let y = 0;
    let dragging = false;
    let lastX = 0;
    let lastY = 0;

    const viewer = document.createElement('div');
    viewer.className = 'diagram-modal';
    viewer.setAttribute('role', 'dialog');
    viewer.setAttribute('aria-modal', 'true');
    viewer.setAttribute('aria-label', 'Visor de diagramas');
    viewer.hidden = true;

    viewer.innerHTML = `
    <div class="diagram-toolbar">
      <button type="button" data-action="in">+</button>
      <button type="button" data-action="out">−</button>
      <button type="button" data-action="reset">Restablecer</button>
      <button type="button" data-action="close">Cerrar ×</button>
    </div>
    <div class="diagram-stage">
      <img class="diagram-full" alt="">
    </div>
  `;

    document.body.appendChild(viewer);

    const stage = viewer.querySelector('.diagram-stage');
    const image = viewer.querySelector('.diagram-full');
    let previousFocus = null;

    function render() {
        image.style.transform =
            `translate(${x}px, ${y}px) scale(${scale})`;
    }

    function reset() {
        scale = 1;
        x = 0;
        y = 0;
        render();
    }

    function open(source) {
        previousFocus = document.activeElement;
        image.src = source.currentSrc || source.src;
        image.alt = source.alt || 'Diagrama ampliado';

        reset();
        viewer.hidden = false;
        document.body.style.overflow = 'hidden';
        viewer.querySelector('[data-action="close"]').focus();
    }

    function close() {
        viewer.hidden = true;
        image.removeAttribute('src');
        document.body.style.overflow = '';
        dragging = false;
        previousFocus?.focus();
    }

    function zoom(factor) {
        scale = Math.min(5, Math.max(0.5, scale * factor));
        render();
    }

    document.addEventListener('click', event => {
        const source = event.target.closest('img.zoomable-diagram');

        if (source) {
            event.preventDefault();
            open(source);
        }
    });

    viewer.addEventListener('click', event => {
        const button = event.target.closest('[data-action]');

        if (button) {
            const action = button.dataset.action;

            if (action === 'in') zoom(1.25);
            if (action === 'out') zoom(0.8);
            if (action === 'reset') reset();
            if (action === 'close') close();

            return;
        }

        if (event.target === viewer) close();
    });

    stage.addEventListener('wheel', event => {
        event.preventDefault();
        zoom(event.deltaY < 0 ? 1.1 : 0.9);
    }, { passive: false });

    stage.addEventListener('pointerdown', event => {
        if (event.button !== 0) return;

        dragging = true;
        lastX = event.clientX;
        lastY = event.clientY;

        stage.setPointerCapture(event.pointerId);
        stage.classList.add('dragging');
    });

    stage.addEventListener('pointermove', event => {
        if (!dragging) return;

        x += event.clientX - lastX;
        y += event.clientY - lastY;

        lastX = event.clientX;
        lastY = event.clientY;

        render();
    });

    function stopDragging() {
        dragging = false;
        stage.classList.remove('dragging');
    }

    stage.addEventListener('pointerup', stopDragging);
    stage.addEventListener('pointercancel', stopDragging);

    document.addEventListener('keydown', event => {
        if (viewer.hidden) return;

        if (event.key === 'Escape') {
            close();
        }

        if (event.key === '+' || event.key === '=') {
            zoom(1.25);
        }

        if (event.key === '-') {
            zoom(0.8);
        }

        if (event.key === '0') {
            reset();
        }

        if (event.key === 'Tab') {
            const buttons = [...viewer.querySelectorAll('button')];
            const first = buttons[0];
            const last = buttons[buttons.length - 1];

            if (event.shiftKey && document.activeElement === first) {
                event.preventDefault();
                last.focus();
            } else if (!event.shiftKey && document.activeElement === last) {
                event.preventDefault();
                first.focus();
            }
        }
    });
})();
