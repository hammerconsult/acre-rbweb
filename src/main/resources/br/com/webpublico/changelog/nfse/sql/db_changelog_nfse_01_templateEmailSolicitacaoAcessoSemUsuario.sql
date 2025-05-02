delete from templatenfse where tipotemplate = 'EMAIL_SOLICITADO_ACESSO_SEM_CADASTRO';
insert into templatenfse values (hibernate_sequence.nextval, 'EMAIL_SOLICITADO_ACESSO_SEM_CADASTRO',
'<div style="padding: 20px; background-color:#f2f2f2;color:#555; font-family:''Helvetica Neue'',''Helvetica'',''Roboto'',''Calibri'',''Arial'',sans-serif">    <div style="padding:10px;font-size:1.1em;margin-top:5px;margin-bottom:5px;">        <h2>Prefeitura do Município $MUNICIPIO</h2>
        <p>Nota Fiscal de Serviços Eletrônica - NFS-e</p>
    </div>
    <div style="line-height:1.5em;text-align:center;background-color:#fff;padding:10px;font-size:1.1em;border:solid .5px #ddd;margin-top:5px;margin-bottom:5px;border-radius:7px">    <h3>Foi encaminhado a você uma solicitação para acesso ao sistema de NFS-E e ISS Online<br/></h3>
	</div>
	    <div style="line-height:1.5em;text-align:left;background-color:#fff;padding:10px;font-size:1.1em;border:solid .5px #ddd;margin-top:5px;margin-bottom:5px;border-radius:7px"><p> <strong>DADOS DO USUÁRIO SOLICITANTE</strong></p>
<table style="width: 100%; text-align: center;">
      <tbody>
         <tr>
            <td style="text-align: justify;">CPF/CNPJ: $CPF_CNPJ_USUARIO</td>
         </tr>
         <tr>
            <td style="text-align: justify;">Nome/Razão Social: $NOME_RAZAOSOCIAL_USUARIO </td>
         </tr>
      </tbody>
</table>
</div>
    <div style="line-height:1.5em;text-align:left;background-color:#fff;padding:10px;font-size:1.1em;border:solid .5px #ddd;margin-top:5px;margin-bottom:5px;border-radius:7px">	<p> <strong>DADOS DO PRESTADOR DE SERVIÇOS</strong></p>
<div>      <table>
         <tbody>
            <tr>
               <td>Nome fantasia: $NOME_FANTASIA_PRESTADOR</td>
            </tr>
            <tr>
               <td>Nome/Razão social: $RAZAO_SOCIAL_PRESTADOR</td>
            </tr>
            <tr>
               <td>CPF/CNPJ: $CPF_CNPJ_PRESTADOR</td>
            </tr>
            <tr>
               <td>Endereço: $LOGRADOURO_PRESTADOR Bairro: $BAIRRO_PRESTADOR CEP: $CEP_PRESTADOR</td>
            </tr>
            <tr>
               <td>Complemento: $COMPLEMENTO_PRESTADOR</td>
            </tr>
            <tr>
               <td>Municí­pio: $MUNICIPIO_PRESTADOR</td>
           </tr>
            <tr>
               <td>E-mail: $EMAIL_PRESTADOR            </td>
</tr>
         </tbody>
      </table>
      <span style="font-size: 11px;">                </span>      </div>
    </div>
	    <div style="line-height:1.5em;text-align:left;background-color:#fff;padding:10px;font-size:1.1em;border:solid .5px #ddd;margin-top:5px;margin-bottom:5px;border-radius:7px">Acesse  $LINK se cadastre para ter acesso<br/></div>
</div>');
