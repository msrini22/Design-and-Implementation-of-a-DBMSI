#This Makefile is used to create assignments.

#the following macros should be updated according to
#the assignment to be generated

JARFILES=bufmgr/*.class diskmgr/*.class global/*.class iterator/*.class\
         heap/*.class chainexception/*.class  btree/*.class index/*.class tests/*.class

JDKPATH = /home/vivemeno/programs/jdk1.8.0_181
LIBPATH = /home/vivemeno/DBMSI/minibase_repo/src/lib
CLASSPATH = $(LIBPATH)
BINPATH = $(JDKPATH)/bin
JAVAC = $(JDKPATH)/bin/javac -classpath $(CLASSPATH)
JAVA  = $(JDKPATH)/bin/java  -classpath $(CLASSPATH)

DOCFILES=bufmgr diskmgr global chainexception heap btree iterator index

##############  update the above for each assignment in making

ASSIGN=/home/vivemeno/DBMSI/minibase_repo/src
LIBDIR=$(ASSIGN)/lib
KEY=$(ASSIGN)/key
SRC=$(ASSIGN)/src

IMAGELINK=$(PACKAGEINDEX)/images
PACKAGEINDEX=$(ASSIGN)/javadoc

JAVADOC=javadoc -public -d $(PACKAGEINDEX)

### Generate jar and javadoc files.  Apply to most assignments.
db: 
	make -C global
	make -C chainexception
	make -C btree
	make -C bufmgr
	make -C diskmgr
	make -C heap
	make -C index
	make -C iterator
	make -C project
	
doc:
	$(JAVADOC) $(DOCFILES)

test: 
	cd tests; make bmtest dbtest; whoami; make hftest bttest indextest jointest sorttest sortmerge

project: 
	cd project; make Phase1

clean:
	\rm -f $(CLASSPATH)/*.class *~ \#* core $(JARFILES) TRACE

