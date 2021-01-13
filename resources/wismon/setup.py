"""
OpenWIS monitoring tool for providing JSON messages to WMO Common Dashboard pilot project.
"""

import os
import sys

from setuptools import setup

from wismon import __version__

BASE_DIR = os.path.dirname(__file__)


def read(filename):
    with open(os.path.join(BASE_DIR, filename)) as f:
        return f.read()


def get_requirements():
    lines = read('requirements.txt').splitlines()
    # argparse is part of stdlib for version 2.7+
    if sys.version_info[0] == 2 and sys.version_info[1] <= 6:
        lines.append('argparse>=1.1')
    return lines


setup(name='wismon',
      version=__version__,
      description='openwis monitoring tool for WMO Common Dashboard',
      long_description=__doc__,
      author='Yang Wang',
      author_email='y.wang@bom.gov.au',
      url='http://wis.bom.gov.au',
      platforms='any',
      packages=['wismon'],
      install_requires=get_requirements(),
      entry_points={
          'console_scripts': ['wismon = wismon.WisMon:main'],
      }
)
