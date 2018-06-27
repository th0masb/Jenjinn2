import os, sys


args = sys.argv[1:]
if len(args) == 2:
    src, target = args
    if (all(os.path.isdir(x) for x in args)):
        print('Copying source directory contents to new file in the target directory...')
        src = src[:-1] if src.endswith('/') else src
        src_folder_name = os.path.split(src)[1]
        target_file_path = os.path.join(target, src_folder_name)
        if os.path.exists(target_file_path):
            try:
                print('Removing file which already exists...')
                os.remove(target_file_path)
            except IsADirectoryError:
                print('Cannot overwrite a directory, process terminated.')
                sys.exit(0)
        with open(target_file_path, 'w') as f:
            print('Writing to ' + target_file_path)
            f.writelines([str(x) + '\n' for x in os.listdir(src)])
        print('Done.')
    else:
        print('Two folders must be passed as cmd line args')
else:
    print('Two folders must be passed as cmd line args')

