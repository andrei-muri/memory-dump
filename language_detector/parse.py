import markdown
import re
from markdown.extensions.codehilite import CodeHiliteExtension
from markdown.extensions.extra import ExtraExtension
from pymdownx.emoji import EmojiExtension
from typing import List
import bleach

parser = markdown.Markdown(extensions=[
    ExtraExtension(),
    CodeHiliteExtension(linenums=True, pygments_style='monokai', use_pygments=True, noclasses=False),
    EmojiExtension()
])


def get_code_blocks(md: str) -> List[str]:
    """
    Extracts code blocks from a mardown
        :param str md: Markdown text
        :return List[str]: List of code blocks
    """
    pattern = re.compile(r"```(?:\w+)?\r?\n(?P<code>.*?)(?=```)?\r?\n```", re.DOTALL)
    code_blocks = []
    matches = re.finditer(pattern, md)
    for match in matches:
        if code := match.group("code"):
            code_blocks.append(code.strip())
            
    return code_blocks


def get_html(md: str) -> str:
    """
    Transforms the markdown input and parses it to html with css style.

    :param str md: Markdown text
    :return str: HTML
    """
    html = parser.convert(md)
    return f"""
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Markdown Output</title>
        <style>{CSS}</style>
    </head>
    <body>
        {html}
    </body>
    </html>
    """




CSS = """
pre { line-height: 125%; }
td.linenos .normal { color: inherit; background-color: transparent; padding-left: 5px; padding-right: 5px; }
span.linenos { color: inherit; background-color: transparent; padding-left: 5px; padding-right: 5px; }
td.linenos .special { color: #000000; background-color: #ffffc0; padding-left: 5px; padding-right: 5px; }
span.linenos.special { color: #000000; background-color: #ffffc0; padding-left: 5px; padding-right: 5px; }
.codehilite .hll { background-color: #49483e }
.codehilite { background: #272822; color: #F8F8F2 }
.codehilite .c { color: #959077 } /* Comment */
.codehilite .esc { color: #F8F8F2 } /* Escape */
.codehilite .g { color: #F8F8F2 } /* Generic */
.codehilite .k { color: #66D9EF } /* Keyword */
.codehilite .l { color: #AE81FF } /* Literal */
.codehilite .n { color: #F8F8F2 } /* Name */
.codehilite .o { color: #FF4689 } /* Operator */
.codehilite .x { color: #F8F8F2 } /* Other */
.codehilite .p { color: #F8F8F2 } /* Punctuation */
.codehilite .ch { color: #959077 } /* Comment.Hashbang */
.codehilite .cm { color: #959077 } /* Comment.Multiline */
.codehilite .cp { color: #959077 } /* Comment.Preproc */
.codehilite .cpf { color: #959077 } /* Comment.PreprocFile */
.codehilite .c1 { color: #959077 } /* Comment.Single */
.codehilite .cs { color: #959077 } /* Comment.Special */
.codehilite .gd { color: #FF4689 } /* Generic.Deleted */
.codehilite .ge { color: #F8F8F2; font-style: italic } /* Generic.Emph */
.codehilite .ges { color: #F8F8F2; font-weight: bold; font-style: italic } /* Generic.EmphStrong */
.codehilite .gr { color: #F8F8F2 } /* Generic.Error */
.codehilite .gh { color: #F8F8F2 } /* Generic.Heading */
.codehilite .gi { color: #A6E22E } /* Generic.Inserted */
.codehilite .go { color: #66D9EF } /* Generic.Output */
.codehilite .gp { color: #FF4689; font-weight: bold } /* Generic.Prompt */
.codehilite .gs { color: #F8F8F2; font-weight: bold } /* Generic.Strong */
.codehilite .gu { color: #959077 } /* Generic.Subheading */
.codehilite .gt { color: #F8F8F2 } /* Generic.Traceback */
.codehilite .kc { color: #66D9EF } /* Keyword.Constant */
.codehilite .kd { color: #66D9EF } /* Keyword.Declaration */
.codehilite .kn { color: #FF4689 } /* Keyword.Namespace */
.codehilite .kp { color: #66D9EF } /* Keyword.Pseudo */
.codehilite .kr { color: #66D9EF } /* Keyword.Reserved */
.codehilite .kt { color: #66D9EF } /* Keyword.Type */
.codehilite .ld { color: #E6DB74 } /* Literal.Date */
.codehilite .m { color: #AE81FF } /* Literal.Number */
.codehilite .s { color: #E6DB74 } /* Literal.String */
.codehilite .na { color: #A6E22E } /* Name.Attribute */
.codehilite .nb { color: #F8F8F2 } /* Name.Builtin */
.codehilite .nc { color: #A6E22E } /* Name.Class */
.codehilite .no { color: #66D9EF } /* Name.Constant */
.codehilite .nd { color: #A6E22E } /* Name.Decorator */
.codehilite .ni { color: #F8F8F2 } /* Name.Entity */
.codehilite .ne { color: #A6E22E } /* Name.Exception */
.codehilite .nf { color: #A6E22E } /* Name.Function */
.codehilite .nl { color: #F8F8F2 } /* Name.Label */
.codehilite .nn { color: #F8F8F2 } /* Name.Namespace */
.codehilite .nx { color: #A6E22E } /* Name.Other */
.codehilite .py { color: #F8F8F2 } /* Name.Property */
.codehilite .nt { color: #FF4689 } /* Name.Tag */
.codehilite .nv { color: #F8F8F2 } /* Name.Variable */
.codehilite .ow { color: #FF4689 } /* Operator.Word */
.codehilite .pm { color: #F8F8F2 } /* Punctuation.Marker */
.codehilite .w { color: #F8F8F2 } /* Text.Whitespace */
.codehilite .mb { color: #AE81FF } /* Literal.Number.Bin */
.codehilite .mf { color: #AE81FF } /* Literal.Number.Float */
.codehilite .mh { color: #AE81FF } /* Literal.Number.Hex */
.codehilite .mi { color: #AE81FF } /* Literal.Number.Integer */
.codehilite .mo { color: #AE81FF } /* Literal.Number.Oct */
.codehilite .sa { color: #E6DB74 } /* Literal.String.Affix */
.codehilite .sb { color: #E6DB74 } /* Literal.String.Backtick */
.codehilite .sc { color: #E6DB74 } /* Literal.String.Char */
.codehilite .dl { color: #E6DB74 } /* Literal.String.Delimiter */
.codehilite .sd { color: #E6DB74 } /* Literal.String.Doc */
.codehilite .s2 { color: #E6DB74 } /* Literal.String.Double */
.codehilite .se { color: #AE81FF } /* Literal.String.Escape */
.codehilite .sh { color: #E6DB74 } /* Literal.String.Heredoc */
.codehilite .si { color: #E6DB74 } /* Literal.String.Interpol */
.codehilite .sx { color: #E6DB74 } /* Literal.String.Other */
.codehilite .sr { color: #E6DB74 } /* Literal.String.Regex */
.codehilite .s1 { color: #E6DB74 } /* Literal.String.Single */
.codehilite .ss { color: #E6DB74 } /* Literal.String.Symbol */
.codehilite .bp { color: #F8F8F2 } /* Name.Builtin.Pseudo */
.codehilite .fm { color: #A6E22E } /* Name.Function.Magic */
.codehilite .vc { color: #F8F8F2 } /* Name.Variable.Class */
.codehilite .vg { color: #F8F8F2 } /* Name.Variable.Global */
.codehilite .vi { color: #F8F8F2 } /* Name.Variable.Instance */
.codehilite .vm { color: #F8F8F2 } /* Name.Variable.Magic */
.codehilite .il { color: #AE81FF } /* Literal.Number.Integer.Long */


"""

