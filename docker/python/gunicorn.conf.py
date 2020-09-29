import os

_ROOT = os.path.abspath(os.path.join(
    os.path.dirname(__file__), '..'))
_VAR = os.path.join(_ROOT, 'var')
_ETC = os.path.join(_ROOT, 'etc')

loglevel = 'error'
errorlog = "-"
accesslog = "-"

bind = '0.0.0.0:5000'
workers = 2

timeout = 10 * 60  # 10 minutes
keepalive = 24 * 60 * 60  # 1 day

capture_output = True
