###############################################################################
#
# upload_bintray.py - script to upload to Bintray
#
###############################################################################

import os
import posixpath
import sys

import requests

if __name__ == '__main__':
    print 'arguments:', sys.argv
    (script, base_dir, user_id, api_key, api_url, repository, package, version) = sys.argv

    session = requests.Session(auth=(user_id, api_key), verify=False)
    headers = {'X-Bintray-Package': package, 'X-Bintray-Version': version}
    for root, dirs, filenames in os.walk(base_dir, False):
        rel_dir = os.path.relpath(root, base_dir).replace('\\', '/')
        for filename in filenames:
            src_path = os.path.join(root, filename)
            with open(src_path, 'rb') as src_file:
                src_data = src_file.read()
            dst_url = api_url + posixpath.normpath(posixpath.join('/', 'content', user_id, repository, version, rel_dir, filename))
            print 'uploading', src_path, 'size', len(src_data), 'to', dst_url
            sys.stdout.flush()
            response = session.put(dst_url, data=src_data, headers=headers)
            print 'response:', response.text
            sys.stdout.flush()
            