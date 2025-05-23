insert into ASSUNTONFSE
values (hibernate_sequence.nextval, 'Sobre', 1, 1);
insert into ASSUNTONFSE
values (hibernate_sequence.nextval, 'Quem pode acessar o ISS Online', 1, 1);

insert into PERGUNTASRESPOSTAS
values (hibernate_sequence.nextval,
        (select id from ASSUNTONFSE where DESCRICAO = 'Sobre'),
        'O que é NFS-e',
        'NFS-e significa Nota Fiscal de Serviços Eletrônica e é um documento de existência exclusivamente digital, gerado e armazenado em sistema próprio da Prefeitura Municipal, com o objetivo de registrar as operações relativas à prestação de serviços. Este documento vem a substituir as Notas Fiscais Convencionais (impressas em papel), autorizadas pelo Município e confeccionadas em gráfica.',
        1,
        1);

insert into PERGUNTASRESPOSTAS
values (hibernate_sequence.nextval,
        (select id from ASSUNTONFSE where DESCRICAO = 'Sobre'),
        'O que é o ISS Online',
        'O ISS Online é um sistema implantado pela Prefeitura para viabilizar o controle do ISS dos contribuintes de forma eletrônica e em tempo real. A implantação do ISS Online simplifica a vida dos prestadores de serviços, dos cidadãos e das empresas da cidade.',
        2,
        1);

insert into PERGUNTASRESPOSTAS
values (hibernate_sequence.nextval,
        (select id from ASSUNTONFSE where DESCRICAO = 'Quem pode acessar o ISS Online'),
        'Prestador de Serviços',
        'Realize seu cadastro no site e credenciamento junto à Prefeitura e comece a emitir a NFS-e e gerar guias de pagamento de ISS, dentre outros serviços on-line.',
        3,
        1);

insert into PERGUNTASRESPOSTAS
values (hibernate_sequence.nextval,
        (select id from ASSUNTONFSE where DESCRICAO = 'Quem pode acessar o ISS Online'),
        'Tomador de Serviços e Empresas de Fora do Município ',
        'Faça seu cadastro no site e consulte as notas fiscais recebidas, verifique a autenticidade de NFS-e e consulte a substituição de RPS em NFS-e. As Empresas de Fora do Município também podem se cadastrar e, além de consultar as notas fiscais recebidas, verificar a autenticidade de NFS-e e consultar a substituição de RPS em NFS-e, também podem gerar a guia de pagamento de ISS devido no município através do sistema.',
        4,
        1);

insert into PERGUNTASRESPOSTAS
values (hibernate_sequence.nextval,
        (select id from ASSUNTONFSE where DESCRICAO = 'Quem pode acessar o ISS Online'),
        'Contador ',
        'Realize seu cadastro no site e credenciamento junto à Prefeitura e comece a emitir a NFS-e e gerar guias de pagamento de ISS, dentre outros serviços on-line.',
        5,
        1);
