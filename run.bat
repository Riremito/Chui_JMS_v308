@echo off
set MS_VERSION=308
set MS_SUBVERSION=0
set WZ_XML_PATH=wz\
@title �����ŃT�[�o�[ v%MS_VERSION%.%MS_SUBVERSION%
set CLASSPATH=.;dist\*
java -server -Dnet.sf.odinms.wzpath=wz\ server.Start
pause
