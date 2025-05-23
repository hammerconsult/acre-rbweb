insert into CONFIGCONTCONTAREINF select HIBERNATE_SEQUENCE.NEXTVAL, contaextra_id, configuracaocontabil_id, retencaomaxima, 'R2020' from CONFIGCONTCONTAREINF where TIPOARQUIVOREINF = 'R2010'
