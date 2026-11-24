
import re
import math
from dataclasses import dataclass


@dataclass
class Trama:
    sensor: str
    valor: float
    unidad: str


P = re.compile(
    r"(?P<sensor>S[0-9]{2});(?P<valor>-?[0-9]+(?:[.][0-9]+)?);(?P<unidad>C|PCT)"
)


def parsear(linea):
    if len(linea) > 80:
        raise ValueError("Trama demasiado larga")

    m = P.fullmatch(linea)

    if not m:
        raise ValueError("Formato inválido")

    valor = float(m.group("valor"))

    if not math.isfinite(valor):
        raise ValueError("Valor no finito")

    return Trama(
        sensor=m.group("sensor"),
        valor=valor,
        unidad=m.group("unidad")
    )


def main():
    tramas = [
        "S01;23.5;C",
        "S02;-2;C",
        "S1;22;C",
        "S01;NaN;C"
    ]

    for s in tramas:
        try:
            print(parsear(s))
        except ValueError as e:
            print(f"{s}: {e}")


if __name__ == "__main__":
    main()
