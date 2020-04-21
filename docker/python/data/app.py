from flask import Flask, request, flash, redirect, abort, Response
import os
from subprocess import Popen, PIPE
from io import StringIO
from werkzeug.utils import secure_filename


UPLOAD_FOLDER= '/app/data'

app = Flask(__name__)
@app.route('/')
def hello_world():
    return 'Hello world!'

@app.route('/data1', methods=["POST", "GET"])
def run_excel_analysis():
    
    p = Popen("/app/extract_data.py awps.xlsx", shell=True, stdout=PIPE)
    output, err = p.communicate(timeout=15)
    print(err)
    print(output)
    return output, err

def test():
    if 'file' not in request.files:
        abort('No file part')
        return redirect(request.url)
    file = request.files['file']
    if file.filename == '':
        flash('No selected file')
        return redirect(request.url)
    if file:
        filename = secure_filename(file.filename)
        file.save(os.path.join(app.config['UPLOAD_FOLDER'], filename))
    else:
        abort(Response("There is an error with the file"))

if __name__ == '__main__':
    app.run(host='0.0.0.0')
