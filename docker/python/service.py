from flask import Flask, request, flash, redirect, abort, Response
import os
from subprocess import Popen, PIPE
from io import StringIO
from werkzeug.utils import secure_filename


UPLOAD_FOLDER= '/app/data/'
UPLOAD_SCRIPT_FOLDER= '/app/scripts/'

app = Flask(__name__)
@app.route('/')
def hello_world():
    return '' # Send empty String back.

@app.route('/update', methods=["POST"])
def update_data():
    if 'file' not in request.files:
        abort(Response('No file part'))
        return redirect(request.url)
    file = request.files['file']
    if file.filename == '':
        flash('No selected file')
        return "No file!"
    if file:
        filename = secure_filename(file.filename)
        file.save(os.path.join(UPLOAD_FOLDER, filename))
    else:
        abort(Response("There is an error with the file"))
    scriptname = request.form.get('script')
    processname = "python "+ UPLOAD_SCRIPT_FOLDER + scriptname + " " + UPLOAD_FOLDER + filename

    p = Popen(processname, shell=True, stdout=PIPE)
    output, err = p.communicate(timeout=600)
    print(err)
    print(output)
    return output, err

@app.route('/save', methods=["POST"])
def create_chart():
    if 'file' not in request.files:
        abort(Response('No file part'))
        return redirect(request.url)
    file = request.files['file']
    if file.filename == '':
        flash('No selected file')
        return "No file!"
    if file:
        filename = secure_filename(file.filename)
        file.save(os.path.join(UPLOAD_SCRIPT_FOLDER, filename))
    else:
        abort(Response("There is an error with the file"))
    return Response("All fine", status=200)

if __name__ == '__main__':
    app.run(host='0.0.0.0')
