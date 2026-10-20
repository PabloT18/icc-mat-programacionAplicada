async function renderMermaid() {
    const blocks = document.querySelectorAll(
        'pre > code.language-mermaid'
    );

    if (!blocks.length) return;

    if (!window.mermaid) {
        console.error('No se pudo cargar la biblioteca Mermaid.');
        return;
    }

    // Esperar las fuentes antes de calcular el tamaño del diagrama.
    await document.fonts.ready;

    mermaid.initialize({
        startOnLoad: false,
        securityLevel: 'strict',
        theme: 'default',
        fontFamily: 'DejaVu Sans',
        flowchart: {
            useMaxWidth: true,
            htmlLabels: false
        }
    });

    for (const block of blocks) {
        const original = block.parentElement;
        const diagram = document.createElement('div');

        diagram.className = 'mermaid';
        diagram.textContent = block.textContent;
        diagram.style.overflowX = 'auto';
        diagram.style.textAlign = 'center';

        original.replaceWith(diagram);

        try {
            await mermaid.run({ nodes: [diagram] });
        } catch (error) {
            // Conservar el código visible si el diagrama tiene errores.
            diagram.replaceWith(original);
            console.error('Error al interpretar el diagrama Mermaid:', error);
        }
    }
}

renderMermaid();