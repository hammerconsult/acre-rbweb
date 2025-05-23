update prefeituraportal set paginainicial = '<p style=''text-align: justify;''>
                            <strong>
                                O que é Portal da Transparência?<br/>
                                <br/>
                                O Portal da Transparência é uma iniciativa da Prefeitura Municipal de Rio Branco, que possibilita o
                                controle social pelos cidadãos ao disponibilizar dados e informações da Administração Pública
                                Municipal, além de oferecer ferramenta para solicitações de informações por meio da Lei de Acesso a
                                Informação (LAI).<br/>
                                <br/>
                                A transparência pública, além de possuir um papel fundamental no combate à corrupção, viabiliza a
                                contribuição tempestiva da sociedade e dos órgãos de controle, no fornecimento de elementos para que o
                                Município se torne cada vez mais eficiente e efetivo. Além disso, estimula o desenvolvimento de uma
                                cultura de integridade na gestão e incentiva o esforço por melhores políticas e programas de governo.<br/>
                                <br/>
                            </strong>
                        </p>'
where nome in ('Controladoria-Geral do Municipio | PMRB');

update prefeituraportal
set nome = 'CONTROLADORIA-GERAL DO MUNICÍPIO DE RIO BRANCO',
    endereco = 'Endereço: Rua Alvorada, n°281, Bosque – 5° Andar <br/>' ||
               'CEP 69900-664 – Rio Branco/AC',
    emailtecnologia = 'E-mail: cgmrb@riobranco.ac.gov.br',
    contatotecnologia = 'Tel.: (68) 3212-7385',
    horarioatendimento = 'SERVIÇO DE INFORMAÇÃO AO CIDADÃO – SIC <br/>' ||
                         'Atendimento presencial de segunda à sexta-feira <br/>' ||
                         'Das 7:00h às 14:00h',
    contato = 'Tel.: (68) 3212-7419'
where nome = 'Controladoria-Geral do Municipio | PMRB';
