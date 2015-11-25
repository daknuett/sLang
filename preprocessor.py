#from memory import *
#from processor import *
import string

STD_INC_PATH="./"
DEBUG=False

""" use an assembly file to programm the flash. """


class Preprocessor(object):
	def __init__(self,fname):
		self.in_file=open(fname,"r")
		self.out_file=open(fname+".pr","w")
		self.symbols={}
		self.unprocessed=self.in_file.read()
		self.lines=self.unprocessed.split("\n")
	def strip_comments(self):
		new_lines=[]
		for line in self.lines:
			if(line.startswith(";")):
				continue
			if(line.isspace()):
				continue
			new_lines.append(line)
		self.lines=new_lines
	def include_files(self):
		includes=[]
		imports=[]
		for line in self.lines:
			if(line.startswith("#include")):
				includes.append(line)
			if(line.startswith("#import")):
				imports.append(line)
		for inc in includes:
			fname=""
			inc=inc[8:]
			if(inc[0]=='"'):
				fname=inc[1:-1]
			elif(inc[0]=="<"):
				fname=STD_INC_PATH+inc[1:-1]
			else:
				raise InvalidSyntaxError(inc)
			f=open(fname)
			new_lines=f.read().split("\n")
			self.lines=new_lines+self.lines
		for imp in imports:
			fname=""
			imp=imp[7:]
			if(imp[0]=='"'):
				fname=imp[1:-1]
			elif(imp[0]=="<"):
				fname=STD_INC_PATH+imp[1:-1]
			else:
				raise InvalidSyntaxError(imp)
			f=open(fname)
			new_lines=f.read().split("\n")
			self.lines=self.lines+new_lines
	def process_definitions(self):
		new_lines=[]
		for line in self.lines:
			new_line=[]
			for word in line.split():
				if(word in self.symbols):
					new_line.append(self.symbols[word])
					if(DEBUG):
						print("preprocessor: substituting {0} with {1}".format(word,self.symbols[word]))
				else:
					new_line.append(word)
			line=" ".join(new_line)
			if(line.startswith("#define")):
				args=line.split()
				self.symbols[args[1]]=" ".join(args[2:])
				continue
			if(line.startswith("#include")):
				continue
			if(line.startswith("#import")):
				continue
			new_lines.append(line)
		self.lines=new_lines
	def write(self):
		self.out_file.write("\n".join(self.lines))
	def do_all(self):
		self.include_files()
		self.process_definitions()
		self.strip_comments()
		self.write()
		self.in_file.close()
		self.out_file.close()
			
				


class InvalidSyntaxError(BaseException):
	def __init__(self,*args):
		BaseException.__init__(self,*args)
class SemanticError(BaseException):
	def __init__(self,*args):
		BaseException.__init__(self,*args)


if __name__=="__main__":
	p=Preprocessor("sample.sLang")
	p.do_all()
