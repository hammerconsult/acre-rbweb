DECLARE
texto CLOB := '<div class="post-content"><hr>
<p style="text-align: justify;"><strong><a href="http://portalcgm.riobranco.ac.gov.br/lai/wp-content/uploads/2022/08/DECRETO-Nº-1586-2024-–-Suspende-cobrança-de-taxa-por-refeição-no-Restaurante-Popular-José-Marques-de-Souza.pdf" target="_blank">DECRETO Nº 1.586 DE 18 DE DEZEMBRO DE 2024</a></strong></p>
<p style="text-align: justify;">“Suspender a cobrança da taxa de manutenção de R$ 2,00 (dois reais) por refeição ofertada pelo Restaurante Popular José Marques de Souza, excepcionalmente no dia 20 de dezembro 2024, como forma de ação natalina a todos os que se encontram em situação de insegurança alimentar no Município de Rio Branco.”</p>
<p style="text-align: justify;"><strong><a href="http://portalcgm.riobranco.ac.gov.br/lai/wp-content/uploads/2022/08/DECRETO-Nº-1903-2022-Suspender-a-cobrança-da-Taxa-de-manutenção-do-Restaurante-Popular-no-dia-23-de-dezembro-de-2022.pdf" target="_blank">DECRETO Nº 1.903 DE 22 DE DEZEMBRO DE 2022</a></strong></p>
<p style="text-align: justify;">“Suspender a cobrança da taxa de manutenção de R$ 2,00 (dois reais) por refeição ofertada pelo Restaurante Popular, excepcionalmente no dia 23 de dezembro 2022, como forma de ação natalina a todos os que se encontram em situação de segurança alimentar no Município de Rio Branco.”</p>
<p style="text-align: justify;"><strong><a href="http://portalcgm.riobranco.ac.gov.br/lai/wp-content/uploads/2022/08/DECRETO-Nº-1232-2022-Taxa-de-manutenção-do-Restaurante-Popular.pdf" target="_blank">DECRETO Nº 1.232 DE 19 DE AGOSTO DE 2022</a></strong></p>
<p style="text-align: justify;">“Fica estabelecido a taxa de manutenção de R$ 2,00 (dois reais) por refeição ofertada pelo Restaurante Popular ser pago pelos beneficiários que serão depositados em conta específica, devendo o poder público municipal cobrir com os custos relacionados, no mínimo, dos gêneros alimentícios, a mão de obra e outros itens de custeio, inclusive impostos, para efetivação das distribuições das refeições.”</p>
<p style="text-align: justify;"><strong><a href="http://portalcgm.riobranco.ac.gov.br/lai/wp-content/uploads/2022/08/DECRETO-Nº-1105-2022-Instituir-a-Reabertura-do-Restaurante-Popular-José-Marques-de-Souza.pdf" target="_blank">DECRETO Nº 1.105 DE 18 DE JULHO DE 2022</a></strong></p>
<p style="text-align: justify;">“Instituir a REABERTURA do Restaurante Popular, denominado Restaurante Popular “José Marques de Souza”, a partir do dia 19 de julho de 2022, destinado a propiciar ás pessoas que se encontram em situação de insegurança alimentar que abrange inscritos no cadastro Único e demais contemplados pela políticas regulamentadas pela Lei Orgânica da Assistência Social – LOAS, refeição diária a preço módico e com qualidade, administrado pela Secretaria Municipal de Assistência Social e Direitos Humanos – SASDH.”</p>
<p style="text-align: justify;"><strong><a href="http://portalcgm.riobranco.ac.gov.br/lai/wp-content/uploads/2022/08/2020-LEI-COMPLEMENTAR-Nº-102-de-23.12.2020-Dispõe-sobre-a-Política-Municipal-de-Segurança-Alimentar-e-Nutricional-PMSAN.pdf" target="_blank">LEI COMPLEMENTAR Nº 102 DE 23 DE DEZEMBRO DE 2020</a></strong></p>
<p style="text-align: justify;">“Dispõe sobre a Política Municipal de Segurança Alimentar e Nutricional no Município de Rio Branco – PMSAN e dá outras providências.”</p>
<p style="text-align: justify;"><strong><a href="http://portalcgm.riobranco.ac.gov.br/lai/wp-content/uploads/2022/08/2008-DECRETO-Nº-2798-2008-Restaurante-Popular.pdf" target="_blank">DECRETO Nº 2.798 DE 03 DE JUNHO DE 2008</a></strong></p>
<p style="text-align: justify;">“Institui o Restaurante Popular e dá outras providências.”</p>
<p style="text-align: justify;"><strong><a href="http://portalcgm.riobranco.ac.gov.br/lai/wp-content/uploads/2022/08/LEI-Nº-1.581-de-19.12.2005-Cria-o-Conselho-de-Segurança-Alimentar-e-Nutricional-CONSEA.pdf" target="_blank">LEI Nº 1.581 DE 19 DE DEZEMBRO DE 2005</a></strong></p>
<p style="text-align: justify;">“Cria o Conselho de Segurança Alimentar e Nutricional – CONSEA Rio Branco.”</p>
</div>';
BEGIN
update PAGINAPREFEITURAPORTAL
set CONTEUDOHTML = texto
where NOME = 'Restaurante Popular' and CHAVE = 'restaurante-popular';
END;
