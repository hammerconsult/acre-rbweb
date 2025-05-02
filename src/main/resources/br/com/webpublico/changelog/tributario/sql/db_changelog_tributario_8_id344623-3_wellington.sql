declare
texto CLOB := '<div><strong>ITBI</strong><br/>O Imposto sobre Transmissão "inter vivos", a qualquer
título, por ato oneroso, de Bens Imóveis deve ser recolhido antes da
escrituração e/ou do registro de imóveis e de direitos a eles relativos
em todas as transmissões onerosas de bens imóveis.</div><div><br/></div><div><strong>INCIDÊNCIA / FATO GERADOR</strong>  </div>Transmissões e cessões onerosas por meio de:<br/>&nbsp;I - A compra e venda;<br/>II - A dação em pagamento; <br/>III - A permuta;<br/>IV
 - Mandato em causa própria, ou com poderes equivalentes, para a
transmissão de bem imóvel e respectivo substabelecimento, ressalvado o
caso de o mandatário receber a escritura definitiva do imóvel;<br/>V - A adjudicação;<br/>VI
 - O valor dos imóveis que, na divisão de patrimônio comum ou na
partilha, forem atribuídos a um dos cônjuges separados ou divorciados,
ao cônjuge supérstite ou a qualquer herdeiro, acima da respectiva meação
 ou quinhão;<br/>VII - As divisões para a extinção de condomínio de bem
imóvel, quando for recebida por qualquer condômino quota-parte material
cujo valor seja maior do que o de sua quota-parte ideal;<br/>VIII - o uso, o usufruto e a enfiteuse;<br/>IX - A cessão de direitos do arrematante ou adjudicatário, depois de assinado o auto de arrematação ou adjudicação;<br/>X - A cessão de direitos decorrentes de compromisso de compra e venda e de promessa de cessão; <br/>XI - A cessão de direitos de concessão real de uso e de uso especial;<br/>XII - A cessão de direitos à sucessão;<br/>XIII - A cessão de benfeitorias e construções em terreno compromissado à venda ou alheio; <br/>XIV - A acessão física quando houver pagamento de indenização;<br/>XV - A cessão de direitos possessórios;<br/>XVI - A promessa de transmissão de propriedades, através de compromisso devidamente quitado;<br/>XVII
 - Todos os demais atos onerosos, translativos de bens imóveis, por
natureza ou acessão física, e constitutivos de direitos reais sobre bens
 imóveis e demais cessões de direitos a eles relativos.<br/><br/><strong>RESPONSÁVEL TRIBUTÁRIO</strong> <br/>O
 ITBI é devido pelo adquirente do bem, direito ou ação; e pelas pessoas
jurídicas cujo patrimônio sejam ou estejam incorporados aos imóveis. São
 responsáveis solidariamente pelo pagamento do imposto devido: o
transmitente e o cedente nas transmissões que se efetuarem sem o
pagamento do imposto. <br/>Nas permutas o imposto será cobrado dos
adquirentes permutantes, tomando-se por base um dos valores permutados,
quando iguais, ou o valor maior, quando diferentes.<br/><br/><strong>BASE DE CÁLCULO / AVALIAÇÃO</strong> <br/>Para
 efeito de recolhimento do ITBI, deverá ser utilizado o valor constante
do instrumento de transmissão ou cessão. No entanto, prevalecerá o valor
 venal do imóvel apurado no exercício, com base na Planta Genérica de
Valores do Município, ou preço de mercado, quando o valor constante do
instrumento de transmissão ou cessão for inferior.<br/>Ressalta-se que
caso sejam omissos ou não mereçam fé às declarações ou os
esclarecimentos prestados ou os documentos expedidos pelo sujeito
passivo, ou pelo terceiro legalmente obrigado, mediante processo
regular, a Administração Pública poderá arbitrar a base de cálculo do
ITBI.<br/><br/><strong>NÃO INCIDÊNCIA / ISENÇÕES</strong><br/>O ITBI não incide sobre a transmissão de bens imóveis ou direitos a eles relativos quando:<br/>I
 - Os adquirentes forem, a União, os Estados, o Distrito Federal, os
Municípios e respectivas autarquias e fundações instituídas e mantidas
pelo Poder Público para atendimento de suas finalidades essenciais;<br/>II - O adquirente for entidade religiosa para atendimento de suas finalidades essenciais;<br/>III
 - Os adquirentes forem partidos políticos, inclusive suas fundações,
entidades sindicais de trabalhadores, instituições de educação e
assistência social sem fins lucrativos que preencham os requisitos da
lei;<br/>IV - Efetuada para incorporação ao patrimônio de pessoas jurídicas em realização de capital; <br/>V - Decorrente de fusão, incorporação, cisão ou extinção de pessoa jurídica;<br/>VI
 - O bem imóvel voltar ao domínio do antigo proprietário por força de
retrovenda, retrocessão, pacto de melhor comprador ou condição
resolutiva, mas não será restituído o imposto que tiver sido pago pela
transmissão originária;<br/>VII – Os casos regulados em leis especiais.<br/><br/><strong>ALÍQUOTA</strong><br/>As alíquotas do ITBI são as seguintes:<br/>I - Transmissões realizadas pelo Sistema Financeiro de Habitação:<br/>a) Sobre o valor efetivamente financiado: 0,5% (meio por cento);<br/>b) Sobre o valor restante: 2% (dois por cento). <br/>II - Demais transmissões: 2% (dois por cento).';
begin
update configuracaotributario set textosolicitacaoitbi = texto;
end;
