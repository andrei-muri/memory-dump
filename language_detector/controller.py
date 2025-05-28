from flask import Flask, request, make_response, jsonify
from parse import get_code_blocks, get_html
from languages import get_statistics
from flask_cors import CORS

app = Flask(__name__)
CORS(app, resources={r"/api/*": {"origins": "http://localhost:8080"}})


@app.post("/api/markdown/html")
def parse_markdown():
    md = request.get_data(as_text=True)
    html = get_html(md)
    response = make_response(html)
    response.headers["Content-Type"] = "text/html; charset=utf-8"
    return response

@app.post("/api/markdown/statistics")
def analysis_markdown():
    md = request.get_data(as_text=True)
    code_blocks = get_code_blocks(md)
    statistics = get_statistics(code_blocks)
    return statistics

if __name__ == "__main__":
    app.run(debug=True)