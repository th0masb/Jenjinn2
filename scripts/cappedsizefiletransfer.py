import sys, os


args = sys.argv[1:]
assert len(args) == 3, "Pass `capsize srcdir dstdir` as args"
assert all(map(os.path.isdir, args[1:])), "Pass a src and dst directory."
try:
	args = [int(args[0])] + args[1:]
except ValueError: 
	print ("Pass an integer cap in kB")
	sys.exit(0)

byteSizeCap, src, dst = 1000 * args[0], *args[1:]
contents = [os.path.join(src, x) for x in os.listdir(src)]
p = lambda x: (not os.path.isdir(x)) and os.path.getsize(x) < byteSizeCap
for path in (x for x in contents if p(x)):
	head, tail = os.path.split(path)
	os.rename(path, os.path.join(dst, tail))


