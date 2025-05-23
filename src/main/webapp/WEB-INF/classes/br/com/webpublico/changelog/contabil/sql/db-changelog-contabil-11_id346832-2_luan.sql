DECLARE
texto CLOB := '<div class="post-content"><hr>
<p><strong><a href="http://portalcgm.riobranco.ac.gov.br/lai/wp-content/uploads/2013/11/LEI-COMPLEMENTAR-Nº-20-DE-17.07.2017-Altera-as-Leis-Municipais-nº-1.817-2010-alterada-pela-Lei-nº-2.013-2013.pdf" target="_blank">LEI COMPLEMENTAR Nº 20 DE 17 DE JULHO DE 2017</a></strong></p>
<p style="text-align: justify;">“Altera a Lei Municipal nº 1.817, de 23 de setembro de 2010, alterada&nbsp;pela Lei Municipal nº 2.013, de 15 de outubro de 2013.”</p>
<p style="text-align: justify;"><strong><a href="http://portalcgm.riobranco.ac.gov.br/lai/wp-content/uploads/2013/11/DECRETO-Nº-236-DE-18-DE-FEVEREIRO-DE-2014.pdf" target="_blank">DECRETO Nº 236 DE 18 DE FEVEREIRO DE 2014</a></strong></p>
<p style="text-align: justify;">“Altera o Decreto n° 3.926, de 31 de agosto de 2012 e Revoga o Decreto nº 1.869, de 17 de Setembro de 2013.”</p>
<p style="text-align: justify;"><strong><a href="http://portalcgm.riobranco.ac.gov.br/lai/wp-content/uploads/2013/11/LEI-Nº-2013-DE-15-DE-OUTUBRO-DE-2013.pdf" target="_blank">LEI Nº 2.013 DE 15 DE OUTUBRO DE 2013</a></strong></p>
<p style="text-align: justify;">“Altera a Lei Municipal n° 1.817, de 23 de setembro de 2010, que dispõe sobre a Administração e Concessão do Uso de Espaços Públicos Municipais e dá outras providências.’’</p>
<p style="text-align: justify;"><strong><a href="http://portalcgm.riobranco.ac.gov.br/lai/wp-content/uploads/2013/11/DECRETO-Nº-1869-DE-17-DE-SETEMBRO-DE-2013.pdf" target="_blank">DECRETO Nº 1.869 DE 17 DE SETEMBRO DE 2013</a></strong></p>
<p style="text-align: justify;">“Altera o Decreto nº 3.926 de 31 de Agosto de 2012.”</p>
<p style="text-align: justify;"><strong><a href="http://portalcgm.riobranco.ac.gov.br/lai/wp-content/uploads/2013/11/LEI-Nº-1.977-DE-13-DE-MAIO-DE-2013.pdf" target="_blank">LEI Nº 1.977 DE 13 DE MAIO DE 2013</a></strong></p>
<p style="text-align: justify;">“Altera a Lei nº 1.817 de 23 de setembro de 2010, que dispõe sobre a Administração e Concessão de Uso dos Espaços Públicos Municipais e dá outras providências”.</p>
<p style="text-align: justify;"><strong><a href="http://portalcgm.riobranco.ac.gov.br/lai/wp-content/uploads/2013/11/DECRETO-Nº-3926-DE-31-DE-AGOSTO-DE-2012-DOE.pdf" target="_blank">DECRETO Nº 3.926 DE 31 DE AGOSTO DE 2012</a></strong>&nbsp;<span style="color: #ff0000;">(Alterado pelos&nbsp;DECRETO Nº 1.869 DE 17 DE SETEMBRO DE 2013&nbsp;e DECRETO Nº 236 DE 18 DE FEVEREIRO DE 2014)</span></p>
<p style="text-align: justify;">“Regulamenta os procedimentos para retomada de espaços públicos em razão da infringência das disposições da Lei nº 1.817, de 23 de setembro de 2010, assim como disciplina a transferência e aquisição originária dos referidos espaços e dá outras providências”.</p>
<p style="text-align: justify;"><strong><a href="http://portalcgm.riobranco.ac.gov.br/lai/wp-content/uploads/2013/11/LEI-Nº-1.859-de-04.11.2011-Concessão-para-a-prestação-de-serviço-público-exploração-e-administração-dos-espaços-dos-terminais.pdf" target="_blank">LEI Nº 1.859 DE 04 DE NOVEMBRO DE 2011</a></strong></p>
<p style="text-align: justify;">“Autoriza o Município a outorgar concessão para a prestação de serviço público, exploração e administração dos espaços dos terminais urbanos e rodoviários no Município de Rio Branco/AC, e dá outras providências.”</p>
<p style="text-align: justify;"><strong><a href="http://portalcgm.riobranco.ac.gov.br/lai/wp-content/uploads/2013/11/LEI_Nº_1.817_de_23.09.2010_-_Concessão_de_Uso_dos_Espaços_Públicos_Municipais.pdf" target="_blank">LEI Nº 1.817 DE 23 DE SETEMBRO DE 2010</a></strong>&nbsp;<span style="color: #ff0000;">(Alterada pela LEI Nº 1.977 DE 13 DE MAIO DE 2013,&nbsp;LEI Nº 2.013 DE 15 DE OUTUBRO DE 2013 e&nbsp;LEI COMPLEMENTAR Nº 20 DE 17 DE JULHO DE 2017)</span></p>
<p style="text-align: justify;">“Dispõe sobre a Administração e Concessão de Uso dos Espaços Públicos Municipais e dá outras providências”.</p>
<p style="text-align: justify;"><strong><a href="http://portalcgm.riobranco.ac.gov.br/lai/wp-content/uploads/2013/11/LEI-Nº-1.512-DE-29-DE-DEZEMBO-DE-2003.pdf" target="_blank">LEI Nº 1.512 DE 29 DE DEZEMBRO DE 2003</a></strong></p>
<p style="text-align: justify;">“Estabelece critérios para realização de eventos públicos em Rio Branco e dá outras providências”.</p>
</div>';
BEGIN
update PAGINAPREFEITURAPORTAL
set CONTEUDOHTML = texto
where NOME = 'Administração e Concessão de Uso dos Espaços Públicos Municipais' and CHAVE = 'administracao-e-concessao-de-uso';
END;
