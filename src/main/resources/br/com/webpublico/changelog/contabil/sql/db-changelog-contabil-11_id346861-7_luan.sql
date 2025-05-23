DECLARE
texto CLOB := '<div class="post-content">
<p style="text-align: center;"><img style="border: 4px solid #ebebeb;" src="/lai/wp-content/uploads/images/Menu_Transporte_e_Transito.jpg" alt="" width="515" height="350"></p>
<p>&nbsp;</p>
<p><strong>Transporte e Trânsito – Serviços RBTRANS</strong></p>
<p><strong>Endereço:</strong>&nbsp;Via Verde 330, Praia do Amapá. CEP.: 69.906-644</p>
<p><strong>CEP:</strong>&nbsp;69.907-420</p>
<p><strong>Fones:</strong>&nbsp;3214-3315</p>
<p><strong>Horário de Funcionamento:</strong> Segunda à Sexta – 8h às 13h</p>
<p>&nbsp;</p>
<p><span style="text-decoration: underline;"><strong>Serviços de Táxi</strong></span></p>
<ul>
<li>Cadastro de permissionário;</li>
<li>Transferência de permissão;</li>
<li>Renovação de permissão;</li>
<li>Inserção de veículo;</li>
<li>Exclusão de veículo;</li>
<li>Inserção de condutor auxiliar;</li>
<li>Exclusão de condutor auxiliar;</li>
<li>Declaração que exerce a atividade de taxista;</li>
<li>Declaração por tempo de serviço;</li>
<li>Atestado para isenção de IPI;</li>
<li>Atestado para isenção de ICMS;</li>
<li>Segunda via da credencial;</li>
<li>Autorização para trafegar com veículo sem as faixas e decalque;</li>
<li>Autorização para o taxidoor.</li>
</ul>
<p>&nbsp;</p>
<p><span style="text-decoration: underline;"><strong>Serviços de Moto-Táxi</strong></span></p>
<ul>
<li>Cadastro de permissionário;</li>
<li>Transferência de permissão;</li>
<li>Renovação de permissão;</li>
<li>Inserção de veículo;</li>
<li>Exclusão de veículo;</li>
<li>Inserção de condutor auxiliar;</li>
<li>Exclusão de condutor auxiliar;</li>
<li>Atestado de que exerce a atividade de moto-taxista;</li>
<li>Declaração por tempo serviço;</li>
<li>Segunda via da credencial;</li>
<li>Solicitação de mudança de ponto;</li>
<li>Solicitação de prazo para inserir novo veículo.</li>
</ul>
<p>&nbsp;</p>
<p><span style="text-decoration: underline;"><strong>Serviços de Frete Pessoa Física</strong></span></p>
<ul>
<li>Cadastro de permissão;</li>
<li>Renovação de permissão;</li>
<li>Inserção de veículo;</li>
<li>Exclusão de veículo;</li>
<li>Declaração de que exerce a atividade de freteiro;</li>
<li>Declaração por tempo de serviço;</li>
<li>Segunda via da Autorização – Frete.</li>
</ul>
<p>&nbsp;</p>
<p><span style="text-decoration: underline;"><strong>Serviços de Frete Pessoa Jurídica</strong></span></p>
<ul>
<li>Inserção de veículo;</li>
<li>Exclusão de veículo.</li>
</ul>
<p>&nbsp;</p>
<p><span style="text-decoration: underline;"><strong>Outros Serviços</strong></span></p>
<ul>
<li>Credencial de estacionamento vaga especial para idoso;</li>
<li>Credencial de estacionamento vaga especial para pessoa com deficiência;</li>
<li>Autorização Especial de Tráfego – AET (pessoa física);</li>
<li>Autorização Especial de Tráfego – AET (pessoa jurídica);</li>
<li>Análise de projeto de sinalização de trânsito (pessoa física);</li>
<li>Análise de projeto de sinalização de trânsito (pessoa jurídica);</li>
<li>Vistoria técnica de implantação de projeto de trânsito ou melhorias viárias;</li>
<li>Interdição de via pública (Pessoa Física);</li>
<li>Interdição de via pública (Pessoa Jurídica);</li>
<li>Interdição de via pública por Entidades Religiosas ou Filantrópicas;</li>
<li>Interdição de via pública por Associações de Moradores de Bairro ou Sindicatos;</li>
<li>Interdição de via pública por Organizações Não – Governamentais;</li>
<li>Interdição de via pública por Instituições Públicas;</li>
<li>Sinalização de trânsito (horizontal e vertical);</li>
<li>Autorização para transporte de pessoas na carroceria;</li>
<li>Autorização para publicidade nos coletivos;</li>
<li>Aumento de frota dos coletivos;</li>
<li>Mudança do itinerário das linhas dos coletivos;</li>
<li>Demarcação de pontos OU abrigos dos coletivos, táxi e moto-táxi;</li>
<li>Remoção de pontos OU abrigos dos coletivos, táxi e moto-táxi.</li>
</ul>
<p>&nbsp;</p>
<p><strong>Serviços RBTRANS, prestados na Central de Serviço Público – OCA RIO BRANCO</strong></p>
<p><strong>Endereço: </strong>Rua Quintino Bocaiúva, nº 299 – Centro – Praça Vermelha – 1º Piso<strong><br>
</strong></p>
<p><strong>Fone: </strong>3214-3310 / 3214-3311</p>
<p><strong>Horário de Funcionamento:</strong> Segunda a Sexta – 8h às 18h</p>
<p>&nbsp;</p>
<p><strong><span style="text-decoration: underline;">Serviços de Táxi</span><br>
</strong></p>
<ul>
<li>Atestado para isenção de ICMS;</li>
<li>Atestado para isenção de IPI.</li>
</ul>
<p>&nbsp;</p>
<p><span style="text-decoration: underline;"><strong>Serviços de Moto-táxi</strong></span></p>
<ul>
<li>Atestado do exercício da atividade de moto-taxista.</li>
</ul>
<p>&nbsp;</p>
<p><span style="text-decoration: underline;"><strong>Outros Serviços</strong></span></p>
<ul>
<li>AET – Autorização Especial de Tráfego;</li>
<li>Interdição de vias públicas;</li>
<li>Credencial de estacionamento em vaga especial para idoso;</li>
<li>Credencial de estacionamento em vaga especial para pessoa com deficiência.</li>
</ul>
</div>';
BEGIN
update PAGINAPREFEITURAPORTAL set CONTEUDOHTML = texto where NOME = 'Transporte e Trânsito - Serviços RBTRANS' and CHAVE = 'transporte-e-transito-rbtrans';
END;
