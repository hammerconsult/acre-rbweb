declare
texto CLOB := '<div>
<div style="text-align: center">
<b>Este portal segue as diretrizes do eMAG (Modelo de Acessibilidade em Governo Eletrônico).</b>
</div><br/>
O termo acessibilidade significa incluir a pessoa com deficiência na participação de atividades como o uso de produtos,
serviços e informações. Alguns exemplos são os prédios com rampas de acesso para cadeira de rodas e banheiros
adaptados para deficientes.<br/><br/>
Na internet, acessibilidade refere-se principalmente às recomendações do WCAG (World Content Accessibility Guide)
do W3C e no caso do Governo Brasileiro ao eMAG (Modelo de Acessibilidade em Governo Eletrônico). O eMAG está
alinhado as recomendações internacionais, mas estabelece padrões de comportamento acessível para sites
governamentais.<br/><br/>
Na parte superior do portal existe uma barra de acessibilidade onde se encontra atalhos de navegação padronizados
e a opção para alterar o contraste. Essas ferramentas estão disponíveis em todas as páginas do portal.<br/><br/>
<b>Atalhos padrões (Chrome)</b>
<ul>
<li>Teclando-se Alt + 1 em qualquer página do portal, chega-se diretamente ao começo do conteúdo principal da página.</li>
<li>Teclando-se Alt + 2 em qualquer página do portal, chega-se diretamente ao início do menu principal.</li>
<li>Teclando-se Alt + 3 em qualquer página do portal, chega-se diretamente em sua busca interna.</li>
<li>Teclando-se Alt + 4 em qualquer página do portal, chega-se diretamente ao rodapé do site.</li>
</ul>
<b>Tamanho do texto</b>
<ul>
<li>Ctrl + + Aumenta o zoom da página.</li>
<li>Ctrl + - Diminui o zoom da página.</li>
<li>Ctrl + 0 Deixa o zoom da pagina padrão.</li>
</ul>
Alguns browsers possibilitam a alteração do tamanho do texto da seguinte forma:
<ul>
<li>Internet Explorer: selecione o Menu Opções "Mostrar: Tamanho do Texto".</li>
<li>Mozilla: selecione o Menu Opções: "Mostrar: Tamanho do Texto".</li>
<li>Opera: selecione o Menu Opções: "Mostrar Zoom".</li>
<li>Safari: selecione o Menu Opções: "Apresentação; Aumentar/Reduzir tamanho do texto".</li>
</ul>
<b>Compatibilidade com browsers</b><br/>
Este portal é compatível com todos os navegadores modernos:<br/>
Firefox 2+, Internet Explorer 7+, Safari, Konqueror, Opera 10+, Chrome 1+<br/><br/>
<b>Leis e decretos sobre acessibilidade</b>
<ul>
<li><a href=''http://www.planalto.gov.br/ccivil_03/_Ato2004-
2006/2004/Decreto/D5296.htm'' target=''_blank''>Decreto nº 5.296 de 02 de dezembro de 2004</a></li>
<li><a href=''http://www.planalto.gov.br/ccivil_03/_ato2007-
2010/2009/decreto/d6949.htm'' target=''_blank''>Decreto nº 6.949, de 25 de agosto de 2009 - Promulga a Convenção Internacional sobre os Direitos das Pessoas com Deficiência e seu Protocolo Facultativo, assinados em Nova York, em 30 de março de 2007</a></li>
<li><a href=''http://www.planalto.gov.br/ccivil_03/_ato2011-
2014/2012/Decreto/D7724.htm'' target=''_blank''>Decreto nº 7.724, de 16 de Maio de 2012 - Regulamenta a Lei Nº 12.527, que dispõe sobre o acesso a informações</a></li>
<li><a href=''http://emag.governoeletronico.gov.br/'' target=''_blank''>Modelo de Acessibilidade de Governo Eletrônico</a></li>
<li><a href=''https://www.gov.br/governodigital/pt-br/legislacao/portaria3_eMAG.pdf'' target=''_blank''>Portaria nº 03, de 07 de Maio de 2007 - Institucionaliza o Modelo de Acessibilidade em Governo Eletrônico - e-AG.</a></li>
</ul>
<b>Dúvidas, sugestões e críticas</b><br/>
No caso de problemas com a acessibilidade do portal, favor acessar a <a href=''http://eouv.riobranco.ac.gov.br/index.aspx?ReturnUrl=%2f'' target=''_blank''>Página de Contato</a><br/><br/>
Dicas, links e recursos úteis:
<ul>
<li><a href=''http://acessibilidadelegal.com/'' target=''_blank''>Acessibilidade Legal</a></li>
<li><a href=''https://all-best-betting-sites.com/'' target=''_blank''>Acesso Digital</a></li>
</ul>
</div>';
begin
update paginaprefeituraportal
set conteudohtml = texto
where nome = 'Acessibilidade'
  and chave = 'acessibilidade';
end;
