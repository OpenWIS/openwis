"""
OpenWIS monitoring tool for providing JSON messages to WMO Common Dashboard pilot project.
"""

import os
import sys

from setuptools import setup

BASE_DIR = os.path.dirname(__file__)

def get_version():
    with open(os.path.join(BASE_DIR, 'wismon', '__init__.py')) as ins:
        for line in ins.readlines():
            if line.startswith('__version__'):
                return line.split('=')[1].strip()[1:-1]


def get_requirements():
    with open(os.path.join(BASE_DIR, 'requirements.txt')) as ins:
        lines = ins.read().splitlines()
    # argparse is part of stdlib for version 2.7+
    if sys.version_info[0] == 2 and sys.version_info[1] <= 6:
        lines.append('argparse>=1.1')
    return lines


setup(name='wismon',
      version=get_version(),
      description='openwis monitoring tool for WMO Common Dashboard',
      long_description=__doc__,
      author='Yang Wang',
      author_email='y.wang@bom.gov.au',
      url='http://wis.bom.gov.au',
      platforms='any',
      packages=['wismon'],
      install_requires=get_requirements(),
      data_files=[('wismon', ['wismon/config_template.cfg'])],
      entry_points={
          'console_scripts': ['wismon = wismon:main'],
      }
)
