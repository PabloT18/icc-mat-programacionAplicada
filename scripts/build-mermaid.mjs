
import fs from 'node:fs/promises';
import path from 'node:path';
import { fileURLToPath } from 'node:url';
import { spawn } from 'node:child_process';

// Raíz del proyecto, independientemente de dónde se ejecute.
const __dirname = path.dirname(fileURLToPath(import.meta.url));
const root = path.resolve(__dirname, '..');

// Configuración de Mermaid
const config = path.join(root, 'scripts', 'mermaid-config.json');

// Archivo recibido como argumento
const input = process.argv[2];

if (!input) {
    console.error('Uso: npm run build:mermaid -- <archivo.mmd>');
    process.exit(1);
}

const inputPath = path.resolve(process.cwd(), input);
const extension = path.extname(inputPath).toLowerCase();

if (extension !== '.mmd') {
    console.error('Error: el archivo debe tener extensión .mmd');
    process.exit(1);
}

const basePath = inputPath.slice(0, -extension.length);
const svgPath = `${basePath}-mmd.svg`;
const pngPath = `${basePath}-mmd.png`;

// Ejecutar Mermaid CLI
function runMermaid(output, background, scale = 1) {
    return new Promise((resolve, reject) => {
        const args = [
            'mmdc',
            '-i', inputPath,
            '-o', output,
            '-c', config,
            '-b', background,
            '-s', String(scale),
        ];

        const command = process.platform === 'win32'
            ? 'npx.cmd'
            : 'npx';

        const child = spawn(command, ['--no-install', ...args], {
            cwd: root,
            stdio: 'inherit',
        });

        child.on('error', reject);

        child.on('close', (code) => {
            if (code === 0) {
                resolve();
            } else {
                reject(new Error(`Mermaid CLI terminó con código ${code}`));
            }
        });
    });
}

async function main() {
    try {
        await fs.access(inputPath);
        await fs.access(config);

        console.log(`Archivo: ${inputPath}`);

        console.log('\nGenerando SVG...');
        await runMermaid(svgPath, 'transparent');

        console.log('\nGenerando PNG...');
        await runMermaid(pngPath, 'white', 3);

        console.log('\nDiagramas generados correctamente:');
        console.log(`SVG: ${svgPath}`);
        console.log(`PNG: ${pngPath}`);

    } catch (error) {
        console.error(`\nError: ${error.message}`);
        process.exitCode = 1;
    }
}

await main();
