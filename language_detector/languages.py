from guesslang import Guess
from collections import defaultdict
from typing import List, DefaultDict, Dict

guesser = Guess()

def get_statistics(code_blocks: List[str]):
    """
    Takes the list of code blocks, and returns a dict containing a field with
    a list of two-item lists,
    the first element being the language name and second the percentahe

    :param List[str] code_blocks: list of code blocks
    :return Dict[str, List[List[str]]]:
    """
    map_language_size: DefaultDict[str, int] = defaultdict(int)
    total_length = 0

    for block in code_blocks:
        lang: str = guesser.language_name(block)
        map_language_size[lang] += len(block)

    total_length = sum(map_language_size.values())

    map_language_percentage: Dict[str, str] = {}
    statistics = []
    for k, v in map_language_size.items():
        percentage = f"{(v / total_length) * 100 : .2f}"
        map_language_percentage[k] = percentage
        statistics.append([k, percentage])
    lang = {}
    lang["statistics"] = statistics
    return lang
