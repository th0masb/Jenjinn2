import os, sys, zipfile

args = sys.argv[1:]
if len(args) == 0:
    print("No folder path was passed.")
else:
    folder_path = os.path.join(args[0])
    if os.path.exists(folder_path) and os.path.isdir(folder_path):
        print("Unzipping .zip archives in " + folder_path)
        zips = [x for x in os.listdir(folder_path) if x.endswith('.zip')]
        for zip_file in zips:
            pth = os.path.join(folder_path, zip_file)
            print('Unzipping ' + pth + '...')
            z = zipfile.ZipFile(pth)
            z.extractall(folder_path)
            os.remove(pth)
        print('Done')
    else:
        print('Specified path does not lead to a folder')

