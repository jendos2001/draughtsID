import pandas
import torch
import CONSTANTS
import base64
from PIL import Image
from os.path import dirname, join
import io


def checkPosition(df: pandas.DataFrame, isVertical: bool, isBlack: bool) -> dict:
    conf = -1
    for i, row in df.iterrows():
        if row['name'] == "Board":
            if row.confidence > conf:
                xBoard_start = df.loc[i].xmin
                xBoard_end = df.loc[i].xmax
                yBoard_start = df.loc[i].ymin
                yBoard_end = df.loc[i].ymax
                conf = row.confidence
    x_row_size = (xBoard_end - xBoard_start) / 8
    y_row_size = (yBoard_end - yBoard_start) / 8
    df = df[df.name != 'Board']
    figures = {
        'black': {
            'draught': {},
            'king': {}
        },
        'white': {
            'draught': {},
            'king': {}
        }
    }
    for i, row in df.iterrows():
        figure_type = row['name'].split('_')
        x_center = (row.xmin + row.xmax) / 2
        y_center = (row.ymin + row.ymax) / 2
        x_coord = -1
        y_coord = -1
        for j in range(8):
            if float(xBoard_start + j * x_row_size) <= x_center <= float(xBoard_start + (j + 1) * x_row_size):
                x_coord = j
                break
        for j in range(8):
            if float(yBoard_start + j * y_row_size) <= y_center <= float(yBoard_start + (j + 1) * y_row_size):
                y_coord = j
                break
        if x_coord != -1 and y_coord != -1:
            if isVertical:
                if isBlack:
                    cell = f"{CONSTANTS.rowLetters[7 - x_coord]}{y_coord + 1}"
                    if cell in CONSTANTS.wrongFields:
                        continue
                    if cell not in figures[figure_type[0].lower()][figure_type[1].lower()].keys():
                        figures[figure_type[0].lower()][figure_type[1].lower()][cell] = row.confidence
                    else:
                        if row.confidence > figures[figure_type[0].lower()][figure_type[1].lower()][cell]:
                            figures[figure_type[0].lower()][figure_type[1].lower()][cell] = row.confidence
                else:
                    cell = f"{CONSTANTS.rowLetters[x_coord]}{8 - y_coord}"
                    if cell in CONSTANTS.wrongFields:
                        continue
                    if cell not in figures[figure_type[0].lower()][figure_type[1].lower()].keys():
                        figures[figure_type[0].lower()][figure_type[1].lower()][cell] = row.confidence
                    else:
                        if row.confidence > figures[figure_type[0].lower()][figure_type[1].lower()][cell]:
                            figures[figure_type[0].lower()][figure_type[1].lower()][cell] = row.confidence
            else:
                if isBlack:
                    cell = f"{CONSTANTS.rowLetters[7 - y_coord]}{8 - x_coord}"
                    if cell in CONSTANTS.wrongFields:
                        continue
                    if cell not in figures[figure_type[0].lower()][figure_type[1].lower()].keys():
                        figures[figure_type[0].lower()][figure_type[1].lower()][cell] = row.confidence
                    else:
                        if row.confidence > figures[figure_type[0].lower()][figure_type[1].lower()][cell]:
                            figures[figure_type[0].lower()][figure_type[1].lower()][cell] = row.confidence
                else:
                    cell = f"{CONSTANTS.rowLetters[y_coord]}{x_coord + 1}"
                    if cell in CONSTANTS.wrongFields:
                        continue
                    if cell not in figures[figure_type[0].lower()][figure_type[1].lower()].keys():
                        figures[figure_type[0].lower()][figure_type[1].lower()][cell] = row.confidence
                    else:
                        if row.confidence > figures[figure_type[0].lower()][figure_type[1].lower()][cell]:
                            figures[figure_type[0].lower()][figure_type[1].lower()][cell] = row.confidence
    return figures


# FEN Формат: "(W/B):W(Ka3/a3):B(Ka3/a3)"
def figuresToFEN(figures: dict, option: bool = False) -> str:
    white_figures = []
    black_figures = []
    for white_draught in figures['white']['draught'].keys():
        white_figures.append(white_draught)
    for black_draught in figures['black']['draught'].keys():
        black_figures.append(black_draught)
    for white_king in figures['white']['king'].keys():
        white_figures.append(f"K{white_king}")
    for black_king in figures['black']['king'].keys():
        black_figures.append(f"K{black_king}")
    if not option:
        return f"W:W{','.join(white_figures)}:B{','.join(black_figures)}|" \
               f"B:W{','.join(white_figures)}:B{','.join(black_figures)}"
    else:
        return f"W{','.join(white_figures)}:B{','.join(black_figures)}"


def posToFEN(dfList: list, isVertical: bool, isBlack: bool) -> str:
    s = ""
    for elem in dfList:
        if not (elem.name == 'Board').any():
            s += f"Не удалось распознать позицию"
        else:
            s += f"{figuresToFEN(checkPosition(elem, isVertical, isBlack))}"
    return s


def getDiff(dfList: list, isVertical: bool, isBlack: bool, prevPos: str, isWhiteMove: bool) -> str:
    print(prevPos)
    cur = ""
    for elem in dfList:
        if not (elem.name == 'Board').any():
            return f"Не удалось найти доску|"
        else:
            cur += f"{figuresToFEN(checkPosition(elem, isVertical, isBlack), True)}"
    print(cur)
    prevPos = prevPos[2:].split(":")
    print(prevPos)
    whitePrev = set(prevPos[0][1:].split(","))
    blackPrev = set(prevPos[1][1:].split(","))
    s = cur.split(":")
    whiteCur = set(s[0][1:].split(","))
    blackCur = set(s[1][1:].split(","))
    print(whiteCur, blackCur, whitePrev, blackPrev)
    diffWhite = whitePrev ^ whiteCur
    diffBlack = blackPrev ^ blackCur
    print(diffWhite, diffBlack)
    if len(diffWhite) == 0 and len(diffBlack) == 0:
        return f"|{cur}"
    if isWhiteMove:
        if len(diffWhite) == 2:
            if len(diffBlack) == 0:
                if list(diffWhite)[0] in whitePrev:
                    return getMoveByPos(list(diffWhite)[0], list(diffWhite)[1], 0) + f"|W:{cur}"
                else:
                    return getMoveByPos(list(diffWhite)[1], list(diffWhite)[0], 0) + f"|W:{cur}"
            else:
                if list(diffWhite)[0] in whitePrev:
                    return getMoveByPos(list(diffWhite)[0], list(diffWhite)[1], 1) + f"|W:{cur}"
                else:
                    return getMoveByPos(list(diffWhite)[1], list(diffWhite)[0], 1) + f"|W:{cur}"
    else:
        if len(diffBlack) == 2:
            if len(diffWhite) == 0:
                if list(diffBlack)[0] in blackPrev:
                    return getMoveByPos(list(diffBlack)[0], list(diffBlack)[1], 0) + f"|W:{cur}"
                else:
                    return getMoveByPos(list(diffBlack)[1], list(diffBlack)[0], 0) + f"|W:{cur}"
            else:
                if list(diffBlack)[0] in blackPrev:
                    return getMoveByPos(list(diffBlack)[0], list(diffBlack)[1], 1) + f"|W:{cur}"
                else:
                    return getMoveByPos(list(diffBlack)[1], list(diffBlack)[0], 1) + f"|W:{cur}"
    return f"|W:{cur}"


# 0 - move, 1 - take
def getMoveByPos(pos1: str, pos2: str, type: int) -> str:
    if pos1[0] == 'K':
        pos1 = pos1[1:]
    if pos2[0] == 'K':
        pos2 = pos2[1:]
    if type == 0:
        return f"{pos1}-{pos2}"
    else:
        return f"{pos1}:{pos2}"


def start(img, pos, pos2) -> str:
    image = Image.open(io.BytesIO(base64.decodebytes(bytes(img, "utf-8"))))
    weights_name = join(dirname(__file__), "best.pt")
    model = torch.hub.load('ultralytics/yolov5', 'custom', weights_name)
    model.conf = 0.6
    results = model(image)
    print(results.pandas().xyxy, pos, pos2)
    return posToFEN(results.pandas().xyxy, pos == 0, pos2 == 1)


def getMove(img, pos, pos2, prevPos, isWhiteMove) -> str:
    image = Image.open(io.BytesIO(base64.decodebytes(bytes(img, "utf-8"))))
    weights_name = join(dirname(__file__), "best.pt")
    model = torch.hub.load('ultralytics/yolov5', 'custom', weights_name)
    model.conf = 0.6
    results = model(image)
    return getDiff(results.pandas().xyxy, pos == 0, pos2 == 1, prevPos, isWhiteMove == 0)
