CREATE TABLE USUARIOALVARASEKER
(
    IdUsuario        NUMBER(19) NOT NULL PRIMARY KEY,
    NomeUsuario      VARCHAR(57),
    CargoUsuario     VARCHAR(48),
    LoginUsuario     VARCHAR(18),
    SenhaUsuario     VARCHAR(32) NOT NULL,
    CpfUsuario       VARCHAR(14),
    MatUsuario       VARCHAR(10),
    TelUsuario       VARCHAR(14),
    CelUsuario       VARCHAR(14),
    EmailUsuario     VARCHAR(38),
    DataCadastro     VARCHAR(10) NOT NULL,
    IdUniOrg         NUMBER(19) NOT NULL,
    EmailAlternativo VARCHAR(39),
    FIELD14          NUMBER(19)
);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (1, 'Raphael Luiz Bastos Junior', 'Desenvolvedor', 'rbjunior', 'a45958517604f5cd90d6ee51ad9cfdb6', NULL, NULL,
        '3211-2214', '9229-0886', 'rbjunior@riobranco.ac.gov.br', '10/01/2013', 67, NULL, 0);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (2, 'Gonçalo Gonçalves Duarte', 'TESTE', 'gonca', '28276a521c20d0adbfb0ea4afe2c5aee', '571.434.471-53', '545488',
        NULL, '9971-8891', 'dti@riobranco.ac.gov.br', '21/02/2013', 4, NULL, 0);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (3, 'Clicia Q. Freitas', 'Gerente de Sistemas', 'cqfreitas', 'inativo', '637.916.682', '544.769-1', '32234555',
        '99910282', 'cqfreitas@riobranco.ac.gov.br', '21/03/2007', 28, NULL, NULL);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (4, 'Alessandro de Souza Campos', 'Gestor', 'ascampos', 'f9b9b27440c4910e503621748879a003', NULL, NULL,
        '32127040', NULL, 'ascampos@riobranco.ac.gov.br', '21/03/2007', 68, NULL, NULL);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (5, 'Gonçalo Gonçalves Duarte', 'Diretor', 'ggduarte', '901ad315e92143d381f1c9a226470251', '571.434.471-53',
        '545488', '(68)-3211-2207', '(68)-9976-', 'dti@riobranco.ac.gov.br', '21/02/2013', 4, NULL, 0);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (7, 'Urias Carléo da Costa', 'Auxiliar Administrativo', 'uccosta', '28276a521c20d0adbfb0ea4afe2c5aee',
        '52686191287', '544840-1', '3221-4717', NULL, '28064212@bol.com.br', '14/11/2006', 304, NULL, NULL);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (8, 'Wisgrey Antônio de Lima Braga', 'Agente Administrativo', 'wlbraga', 'e5606dfd4d68db8b3d696d0b715892de',
        '48445908200', '32991601', '3223-9480', NULL, 'wysgrey@hotmail.com', '14/11/2006', 315, NULL, NULL);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (9, 'Oziany Silva de Lima', 'Assistente Administrativo', 'oslima', '28276a521c20d0adbfb0ea4afe2c5aee',
        '753.657.742-72', '535251-01', NULL, NULL, 'oslima@riobranco.ac.gov.br', '23/09/2008', 304, NULL, NULL);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (10, 'Tereza Jesus Canizo de Souza', 'Enfermeira', 'tcsouza', '28276a521c20d0adbfb0ea4afe2c5aee', NULL, NULL,
        '9961-2821', NULL, NULL, '06/12/2006', 299, NULL, NULL);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (11, 'Mauro Hashimoto', 'Médico', 'mmhashimoto', '83b4ef5ae4bb360c96628aecda974200', '5.179.419', NULL,
        '3226-5809', NULL, NULL, '06/12/2006', 299, NULL, NULL);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (12, 'Sônia Oliveira da Cunha', 'Coordenadora', 'socunha', '28276a521c20d0adbfb0ea4afe2c5aee', NULL, NULL,
        '3223-9480', NULL, NULL, '06/12/2006', 315, NULL, NULL);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (13, 'Maria do Carmo Leitão de Lima', 'Coordenadora', 'mllima', '28276a521c20d0adbfb0ea4afe2c5aee', NULL, NULL,
        '3221-4717', NULL, NULL, '06/12/2006', 304, NULL, NULL);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (14, 'Marinilza Manaitá Brandão', 'Coordenadora', 'mmbrandao', '28276a521c20d0adbfb0ea4afe2c5aee', NULL, NULL,
        NULL, '8405-8054', NULL, '06/12/2006', 299, NULL, NULL);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (15, 'Simone Maria Silva Borges', 'Assistente Administrativo', 'ssborges', '28276a521c20d0adbfb0ea4afe2c5aee',
        NULL, NULL, '3224-9529', NULL, NULL, '06/12/2006', 307, NULL, NULL);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (16, 'Hernani Barros Pérez', 'Técnico em Educação', 'hbperez', '647431b5ca55b04fdf3c2fce31ef1915', NULL, NULL,
        NULL, '9987-4353', NULL, '06/12/2006', 307, NULL, NULL);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (17, 'Dion Alves de Oliveira', 'Digitador', 'daoliveira', '28276a521c20d0adbfb0ea4afe2c5aee', '88102742291',
        NULL, NULL, NULL, NULL, '06/12/2006', 308, NULL, NULL);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (18, 'Ana Cristina Miranda de Oliveira', 'Enfermeira', 'amoliveira', '28276a521c20d0adbfb0ea4afe2c5aee',
        '50819432204', NULL, '3224-9529', NULL, 'amoliveira@riobranco.ac.gov.br', '06/12/2006', 307, NULL, NULL);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (19, 'Mauro Oliveira da Rocha', 'Agente Administrativo', 'morocha', '28276a521c20d0adbfb0ea4afe2c5aee',
        '21735158291', '95184-1', '32262664', NULL, 'morocha@riobranco.ac.gov.br', '11/01/2007', 306, NULL, NULL);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (20, 'Estevão Lima Mendes', 'Assistente Administrativo', 'elmendes', '6209804952225ab3d14348307b5a4a27',
        '80988881268', '5447751', '32262664', NULL, 'el.mendes@bol.com.br', '11/01/2007', 306, NULL, NULL);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (21, 'Josenildo Lima Souza', 'Assistente Administrativo', 'ljsouza', '28276a521c20d0adbfb0ea4afe2c5aee',
        '36041106291', '5447981', '32262664', NULL, 'josenildo.l.souza@bol.com.br', '11/01/2007', 306, NULL, NULL);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (22, 'clicia teste', 'teste', 'clicia', '28276a521c20d0adbfb0ea4afe2c5aee', NULL, NULL, NULL, NULL, NULL,
        '12/01/2007', 4, NULL, NULL);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (23, 'Maria Adiene Peres', 'Ch. Divisão de Atendimento', 'maperes', '8bed8da91ea515b0839ad6fd65ec5dc2',
        '3585115268', NULL, '32112210', NULL, 'maperes@riobranco.ac.gov.br', '12/01/2007', 187, NULL, NULL);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (24, 'Natália Cristina Souza de Araújo', 'Secretaria Administrativa', 'ncaraujo',
        '28276a521c20d0adbfb0ea4afe2c5aee', '69108323291', NULL, '32112210', '99718736', 'ncaraujo@riobranco.ac.gov.br',
        '12/01/2007', 187, NULL, NULL);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (25, 'Edinei Pinheiro Portela', 'Agente Administrativo', 'epportela', 'f5a641407214cce265ede52956c4685b',
        '39143686249', '21504001', '32262664', NULL, NULL, '15/01/2007', 306, NULL, NULL);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (26, 'Maria das Graças Teixeira de Souza', 'Agente Administrativo', 'mgsouza',
        'f80bf05527157a8c2a7bb63b22f49aaa', '49515098220', '32613501', NULL, '99971980', 'gracinhagt@bol.com.br',
        '15/01/2007', 313, NULL, NULL);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (27, 'João Marcos Courentino Maia', 'Agente Administrativo', 'jmmaia', 'f80bf05527157a8c2a7bb63b22f49aaa',
        '52007448220', '5447631', '32281554', NULL, 'jhuba_mais@hotmail.com', '15/01/2007', 313, NULL, NULL);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (28, 'Floracy Moreira dos Santos', 'Gerente do Centro de Saúde', 'fmsantos', '28276a521c20d0adbfb0ea4afe2c5aee',
        '34029893287', NULL, NULL, '99892507', 'floracymoreira@bol.com.br', '17/01/2007', 305, NULL, NULL);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (29, 'Amanda Cristina Reis Lima', 'Agente Administrativo', 'arlima', '091e8b81d4e1d5c22c6468adf8684687',
        '57938148253', '546233', '32264202', NULL, NULL, '17/01/2007', 305, NULL, NULL);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (30, 'Maria Antonia Silva Silveira', 'Agente Administrativo', 'mssilveira', '28276a521c20d0adbfb0ea4afe2c5aee',
        '43488200230', '2923381', NULL, '99115090', NULL, '17/01/2007', 305, NULL, NULL);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (31, 'Carlos Cardoso Modesto', 'Gerente do Centro de Saúde', 'ccmodesto', '28276a521c20d0adbfb0ea4afe2c5aee',
        '13826859200', '207101-1', '32280554', NULL, 'cc-modesto@bol.com.br', '25/01/2007', 313, NULL, NULL);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (32, 'Marcos Vieira do Nascimento', 'Assistente Administrativo', 'mnjunior', 'd41d8cd98f00b204e9800998ecf8427e',
        '684.178.882-68', '545336-01', NULL, NULL, 'marcosjunior-ac@bol.com.br', '13/08/2009', 316, NULL, NULL);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (33, 'Lilian Katiucia V. da Silva', 'Servente', 'kvsilva', '28276a521c20d0adbfb0ea4afe2c5aee', '51146029268',
        NULL, '32282329', NULL, NULL, '25/01/2007', 316, NULL, NULL);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (34, 'Francisco Andrade Cacau Júnior', 'Chefe de Divisão', 'cacau.junior', '28276a521c20d0adbfb0ea4afe2c5aee',
        NULL, '1', '32127050', NULL, 'cacau.junior@riobranco.ac.gov.br', '16/01/2015', 66, NULL, 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (35, 'Antônio Sobral Dourado', 'Ch. de Seção', 'asdourado', '2bca18f97f8dd221bece305903735a8f', NULL, NULL, NULL,
        NULL, 'asdourado@riobranco.ac.gov.br', '26/01/2007', 66, NULL, NULL);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (36, 'Mafran Almeida de Oliveira', 'Diretor do Departamento', 'maoliveira', '199f254251132f4a7e689c485f698b80',
        '33730270206', NULL, '32112207', '99853008', 'maoliveira@riobranco.ac.gov.br', '21/03/2007', 4, NULL, NULL);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (37, 'Francisco das Chagas Gomes do Nascimento (Teddy)', 'Suporte de Sistemas', 'fgnascimento',
        '28276a521c20d0adbfb0ea4afe2c5aee', NULL, NULL, NULL, NULL, 'fgnascimento@riobranco.ac.gov.br', '10/01/2013',
        68, NULL, 0);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (38, 'Francisca Mesquita Souza', 'Assistente Administrativo', 'fmsouza', '28276a521c20d0adbfb0ea4afe2c5aee',
        '52281400263', '54475401', '32248611', NULL, NULL, '15/02/2007', 309, NULL, NULL);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (39, 'Vanuza da Silva Barroso', 'Digitadora', 'vsbarroso', '28276a521c20d0adbfb0ea4afe2c5aee', '47835699249',
        NULL, '32248611', NULL, NULL, '15/02/2007', 309, NULL, NULL);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (40, 'Elizangela Custódia Barros', 'Digitador', 'ecbarros', '28276a521c20d0adbfb0ea4afe2c5aee', '57294429200',
        NULL, '32110925', NULL, 'eliz-barros@hotmail.com', '01/03/2007', 317, NULL, NULL);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (41, 'Marcel Maia Santana', 'Agente Administrativo', 'mmsantana', '28276a521c20d0adbfb0ea4afe2c5aee',
        '75138581287', '544751-01', '32210925', NULL, NULL, '02/03/2007', 317, NULL, NULL);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (42, 'Benedita Christian Damasceno Oliveira', 'Chefe da Divisão de Desenvolvimento', 'bdoliveira',
        'a45958517604f5cd90d6ee51ad9cfdb6', '647.923.662-91', NULL, '32112214', '92298902',
        'benedita.menezes@riobranco.ac.gov.br', '08/01/2014', 29, NULL, 0);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (43, 'Alessandro Teste Suporte', NULL, 'alessandro', '28276a521c20d0adbfb0ea4afe2c5aee', NULL, NULL, NULL, NULL,
        NULL, '0000-00-00', 28, NULL, NULL);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (62, 'Nicson da silva aguiar', 'Técnico', 'nsaguiar', '82ece63ba6f5ea162005539b5487361c', NULL, NULL, '32127103',
        NULL, 'nsaguiar@rioranco.ac.gov.br', '27/03/2007', 69, NULL, NULL);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (58, 'Xaris Demétrio Pimentel', 'A. O. S. D.', 'XOPIMENTEL', '28276a521c20d0adbfb0ea4afe2c5aee', '51229625291',
        NULL, '32287880', NULL, NULL, '23/03/2007', 314, NULL, NULL);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (59, 'Raimundo da Silva Negreiro', 'Agente Administrativo', 'RSNEGREIRO', 'f1405b8e61bcea02c60bb1d376b3e57b',
        '19614160200', '31152901', '32237880', NULL, NULL, '23/03/2007', 314, NULL, NULL);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (60, 'Maria do Rosário Souza Campos', 'Assistente Administrativo', 'MSCAMPOS',
        '28276a521c20d0adbfb0ea4afe2c5aee', '664377268', '185060', '32281497', NULL, NULL, '23/03/2007', 314, NULL,
        NULL);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (48, 'Anselmo Guimarães', 'Gerente de Suporte e Logistica Digital', 'aguimaraes',
        '28276a521c20d0adbfb0ea4afe2c5aee', NULL, NULL, '32112212', NULL, 'aguimaraes@riobranco.ac.gov.br',
        '21/03/2007', 65, NULL, NULL);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (49, 'Raimundo Afonso Melo de Souza', 'Chefe da Divisão', 'rmsouza', '3a9bd890947457770b34f71ca92cf700', NULL,
        NULL, '32112212', NULL, 'rmsouza@riobranco.ac.gov.br', '10/01/2013', 70, NULL, 0);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (50, 'João Joaquim Lima Neto', 'Auxiliar Administrativo', 'jlneto', '79e390cb7a7a42b302c4ff7b7f7c89b2', NULL,
        NULL, '32112212', NULL, 'jlneto@riobranco.ac.gov.br', '21/03/2007', 70, NULL, NULL);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (51, 'Rosevaldo Brilhante Matos', 'Chefe da Divisão', 'rbmatos', '66466ae9069c746f501c5e56d7ac1efb', NULL, NULL,
        '32127102', NULL, 'rbmatos@riobranco.ac.gov.br', '21/03/2007', 69, NULL, NULL);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (52, 'Gabriel Moreira Fideles', 'Tecnico de Suporte', 'gmfideles', '35d8cd4e8e20bd62cf8b9c8a620e2799', NULL,
        NULL, '32127103', NULL, 'gmfideles@riobranco.ac.gov.br', '21/03/2007', 71, NULL, NULL);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (53, 'Alex S. Brilhante', 'Tecnico de Suporte', 'asbrilhante', 'bc601a87177d6aa42b2453c699de03ef', NULL, NULL,
        '32127102', NULL, 'asbrilhante@riobranco.ac.gov.br', '21/03/2007', 71, NULL, NULL);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (54, 'Diego Nery Pinheiro', 'Tecnico de Suporte', 'dmpinheiro', 'a5293b60ee9eeea690f6f68a0c25dbb9', NULL, NULL,
        '32127102', NULL, 'dmpinheiro@riobranco.ac.gov.br', '21/03/2007', 71, NULL, NULL);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (55, 'Carlos Alberto', 'Tecnico de Suporte', 'caflorencio', '28276a521c20d0adbfb0ea4afe2c5aee', NULL, NULL,
        '32127102', NULL, 'caflorencio@riobranco.ac.gov.br', '21/05/2007', 71, NULL, NULL);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (56, 'Raifran Ferreira da Silva', 'Tecnico de Suporte', 'rfsilva', '1e752929b7a92b9995197e47b5d1e0fd', NULL,
        NULL, '32127103', NULL, 'rfsilva@riobranco.ac.gov.br', '30/11/2009', 71, NULL, 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (57, 'Rodrigo Oliveira dos Santos', 'Chefe da Divisão de Georeferenciamento', 'rosantos',
        '28276a521c20d0adbfb0ea4afe2c5aee', NULL, NULL, '32112216', NULL, 'rosantos@riobranco.ac.gov.br', '21/03/2007',
        6, NULL, NULL);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (61, 'Abraão Benício de Oliveira', 'Técnico em Enfermagem', 'ABOLIVEIRA', '28276a521c20d0adbfb0ea4afe2c5aee',
        '52388034272', '54496101', '32287880', NULL, NULL, '23/03/2007', 314, NULL, NULL);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (63, 'Dario Araujo da Silva', 'Assistente Administrativo', 'dsaraujo', '2bc0777ca4a34c899c45a8c626a63c07',
        '39099261268', '544766', NULL, NULL, 'dsaraujo@riobranco.ac.gov.br', '29/03/2007', 308, NULL, NULL);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (64, 'Veridiano Barroso de Souza Filho', 'Desenvolvedor', 'vsfilho', '0659410c7f1a5c531b49cd603b203321',
        '79764622291', NULL, '32112214', NULL, 'vsfilho@riobranco.ac.gov.br', '10/04/2007', 29, NULL, NULL);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (65, 'MGA COMERCIO DE PRODUTOS E MANUTENÇAO DE INFORMATICA LTDA', NULL, 'mga',
        '28276a521c20d0adbfb0ea4afe2c5aee', NULL, NULL, NULL, NULL, 'mga@riobranco.ac.gov.br', '15/01/2015', 29, NULL,
        1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (66, 'NTCONSULT acesso para  fornecedores', NULL, 'ntconsult', '28276a521c20d0adbfb0ea4afe2c5aee', NULL, NULL,
        NULL, NULL, NULL, '21/06/2007', 29, NULL, NULL);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (67, 'Andréia Veluma Souza da Silva', 'Estagiaria', 'assouza', 'b7586c3822dd4c7bdd088deee8ac4626', '85712392904',
        NULL, NULL, NULL, 'assouza@riobranco.ac.gov.br', '27/06/2007', 29, NULL, NULL);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (68, 'Central Com Informática', 'Fornecedor de Equipamentos e Assistência Técnica', 'centralcom',
        '28276a521c20d0adbfb0ea4afe2c5aee', NULL, NULL, '32271060', NULL, 'centralcom@riobranco.ac.gov.br',
        '03/05/2007', 4, NULL, NULL);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (69, 'Maria Cristina Brilhante Batista', 'Secretaria', 'mbbatista', '40c775cb4459235ae5b1c7711fb20327', NULL,
        NULL, '32127102', NULL, 'mbbatista@riobranco.ac.gov.br', '03/09/2012', 69, NULL, 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (70, 'Nádia Maria Lima de Oliveira', 'Agente de Saúde', 'nloliveira', 'd41d8cd98f00b204e9800998ecf8427e',
        '57988366220', '33626201', NULL, NULL, NULL, '25/05/2007', 307, NULL, NULL);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (71, 'Sanclé Gomes de Mesquita', 'Desenvolvedor', 'sgmesquita', '3057d486ebe765edb305f9482fa86edf',
        '91183685220', NULL, '32246914', '84040585', 'sgmesquita', '05/06/2007', 29, NULL, NULL);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (72, 'Cleilton Nunes Sampaio', 'Chefe da Divisão de Des. e Aplicações Básicas', 'cnsampaio',
        '83eb8286cce7b7e9dbd540c339a2e476', NULL, NULL, '32112214', NULL, 'cnsampaio@riobranco.ac.gov.br', '05/07/2007',
        29, NULL, NULL);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (73, 'Mafran Teste', 'Diretor', 'mafran', '28276a521c20d0adbfb0ea4afe2c5aee', NULL, NULL, NULL, NULL,
        'maoliveira@riobranco.ac.gov.br', '12/07/2007', 4, NULL, NULL);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (74, 'Paula Andrea Morelli Fonseca', 'Assistente Adminsitrativo', 'pafonseca',
        'a586924ba0d4e5ad4bb5945aecc0c935', '5125761494', '5447621', '32127050', NULL, 'pafonseca@riobranco.ac.gov.br',
        '02/10/2007', 68, NULL, NULL);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (75, 'Luis Antonio Brasil de Lima', 'WEBMASTER', 'lblima', '28276a521c20d0adbfb0ea4afe2c5aee', NULL, NULL, NULL,
        NULL, 'lblima@riobranco.ac.gov.br', '14/12/2009', 49, NULL, 0);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (76, 'Jorge Ney Fernandes', NULL, 'jnfernandes', '28276a521c20d0adbfb0ea4afe2c5aee', NULL, NULL, '99852160',
        NULL, 'jnfernandes@riobranco.ac.gov.br', '11/10/2007', 396, NULL, NULL);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (85, 'Carlos Renato Marmo', NULL, 'crmarmo', 'e9aff5a7f8c33c0bd0dac57541c1f010', NULL, NULL, NULL, NULL,
        'crmarmo@riobranco.ac.gov.br', '30/10/2007', 6, NULL, NULL);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (84, 'Cleilton - usuário Teste', 'Validador', 'cnsampaiot', '28276a521c20d0adbfb0ea4afe2c5aee', NULL, NULL, NULL,
        NULL, 'cnsampaio@riobranco.ac.gov.br', '23/10/2007', 61, NULL, NULL);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (86, 'Raphael Luiz Bastos Junior', 'Desenvolvedor', 'rbjr', '28276a521c20d0adbfb0ea4afe2c5aee', NULL, NULL, NULL,
        NULL, 'rbjunior@riobranco.ac.gov.br', '31/10/2007', 67, NULL, NULL);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (87, 'Tonismar Jose de Oliveira', 'Chefe do Setor', 'tjoliveira', '28276a521c20d0adbfb0ea4afe2c5aee', NULL, NULL,
        NULL, NULL, 'tjoliveira@riobranco.ac.gov.br', '31/10/2007', 106, NULL, NULL);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (89, 'Sângela Feitosa da Silva Vieira', 'Secretária Administrativa', 'sfsilva',
        '18dab6995d9469284a5cd52ef85510c8', NULL, NULL, NULL, NULL, 'sfsilva@riobranco.ac.gov.br', '12/07/2013', 4,
        NULL, 0);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (90, 'Marcio Verissimo Carvalho Dantas', NULL, 'marcio.dantas', '28276a521c20d0adbfb0ea4afe2c5aee', NULL, NULL,
        NULL, NULL, 'marcio.dantas@riobranco.ac.gov.br', '01/02/2019', 25, NULL, 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (91, 'Rosimeire de Fatima Ribeiro Arantes', NULL, 'rrarantes', '28276a521c20d0adbfb0ea4afe2c5aee', NULL, NULL,
        NULL, NULL, 'rrarantes@riobranco.ac.gov.br', '14/12/2009', 6, NULL, 0);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (92, 'Antonia Francisca de Oliveira - Bete', NULL, 'afoliveira', '28276a521c20d0adbfb0ea4afe2c5aee', NULL, NULL,
        NULL, NULL, 'afoliveira@riobranco.ac.gov.br', '27/11/2007', 0, NULL, NULL);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (93, 'Geruza Brito Sarkis', 'Coordenadora do Centro de Saúde', 'gbsarkis', '28276a521c20d0adbfb0ea4afe2c5aee',
        '51493934287', '54643602', '32486259', '99864794', 'casouza@riobranco.ac.gov.br', '27/11/2007', 312, NULL,
        NULL);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (94, 'José Fernandes do Rêgo', NULL, 'jfrego', '28276a521c20d0adbfb0ea4afe2c5aee', NULL, NULL, NULL, NULL,
        'jfrego@riobranco.ac.gov.br', '28/11/2007', 0, NULL, NULL);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (95, 'Elias Mansour Macedo', NULL, 'emmacedo', '28276a521c20d0adbfb0ea4afe2c5aee', NULL, NULL, NULL, NULL,
        'emmacedo@riobranco.ac.gov.br', '30/11/2007', 99, NULL, NULL);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (96, 'Flavia Burlamaqui Machado', NULL, 'fbmachado', '4e59f7020b943c75a7ab6166d08a0e34', NULL, NULL, NULL,
        '84056247', 'fbmachado@riobranco.ac.gov.br', '30/11/2007', 279, NULL, NULL);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (97, 'Raquel Lima da Silva', NULL, 'rlsilva', '28276a521c20d0adbfb0ea4afe2c5aee', NULL, NULL, NULL, '9985-7357',
        'rlsilva@riobranco.ac.gov.br', '30/11/2007', 52, NULL, NULL);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (98, 'Mario Jorge da Silva Fadell', NULL, 'mjfadell', '28276a521c20d0adbfb0ea4afe2c5aee', NULL, NULL, NULL, NULL,
        'mjfadell@riobranco.ac.gov.br', '30/11/2007', 219, NULL, NULL);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (99, 'Marlucia Silva de Souza', NULL, 'mssouza', '28276a521c20d0adbfb0ea4afe2c5aee', NULL, NULL, NULL, NULL,
        'mssouza@riobranco.ac.gov.br', '30/11/2007', 219, NULL, NULL);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (101, 'Renilson Rodrigues de Andrade', NULL, 'rrandrade', '28276a521c20d0adbfb0ea4afe2c5aee', NULL, NULL, NULL,
        NULL, 'rrandrade@riobranco.ac.gov.br', '30/11/2007', 252, NULL, NULL);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (102, 'Vangela Maria do Nascimento', NULL, 'vmnascimento', '8903b2c57ba6ea9e9c390d65bd5091db', NULL, NULL, NULL,
        NULL, 'vmnascimento@riobranco.ac.gov.br', '30/11/2007', 98, NULL, NULL);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (103, 'Paulo Fernandes', NULL, 'pfernandes', '28276a521c20d0adbfb0ea4afe2c5aee', NULL, NULL, NULL, NULL,
        'pfernandes@riobranco.ac.gov.br', '04/12/2007', 11, NULL, NULL);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (104, 'Laura Cristina de Paiva Melo', NULL, 'lpmel', '6ba722d10ef74a55bbd6afb0b542ab0a', NULL, NULL, NULL, NULL,
        'lpmelo@riobranco.ac.gov.br', '12/11/2010', 89, NULL, 0);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (105, 'Leonardo Neder de Faro Freire', NULL, 'lnfreire', '28276a521c20d0adbfb0ea4afe2c5aee', NULL, NULL, NULL,
        NULL, 'lnfreire@riobranco.ac.gov.br', '30/11/2007', 96, NULL, NULL);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (106, 'Clicia Rodrigues da Silva', NULL, 'crsilva', '28276a521c20d0adbfb0ea4afe2c5aee', NULL, NULL, NULL, NULL,
        'crsilva@riobranco.ac.gov.br', '30/11/2007', 3, NULL, NULL);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (107, 'Pablo de Oliveira Mendes', NULL, 'pomendes', 'edf6f38dec4d68e43d05aaba6a6586bc', NULL, NULL, NULL, NULL,
        'pomendes@riobranco.ac.gov.br', '30/11/2007', 72, NULL, NULL);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (108, 'Jose Claudemir Alencar do Nascimento', NULL, 'jlnascimento', 'a1a7c024636b34a0b8f47e4cb46d533c', NULL,
        NULL, NULL, NULL, 'jlnascimento@riobranco.ac.gov.br', '30/11/2007', 293, NULL, NULL);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (109, 'Rondiney Albuquerque Dourado', NULL, 'radourado', '28276a521c20d0adbfb0ea4afe2c5aee', NULL, NULL, NULL,
        NULL, 'radourado@riobranco.ac.gov.br', '30/11/2007', 61, NULL, NULL);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (110, 'Marco Antonio Rodrigues', NULL, 'marodrigues', '28276a521c20d0adbfb0ea4afe2c5aee', NULL, NULL, NULL, NULL,
        'marodrigues@riobranco.ac.gov.br', '30/11/2007', 22, NULL, NULL);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (111, 'Ana Michelly Rodrigues de Souza', NULL, 'amsouza', '1d561dfa1744230ece3e545c35b117b6', NULL, NULL, NULL,
        NULL, 'amsouza@riobranco.ac.gov.br', '30/11/2007', 121, NULL, NULL);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (112, 'Marco Antonio Otsubuo Sanchez', NULL, 'mosanchez', 'b1a097983da35f388351189d8585f3f4', NULL, NULL, NULL,
        NULL, 'mosanchez@riobranco.ac.gov.br', '30/11/2007', 371, NULL, NULL);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (113, 'Rosimara Werner Lemos Duarte', NULL, 'rlduarte', '68124427ce41a87dfcbea86ef95a8c09', NULL, NULL, NULL,
        NULL, 'rlduarte@riobranco.ac.gov.br', '30/11/2007', 289, NULL, NULL);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (114, 'Ocineide Ferreira Machado', NULL, 'ofmachado', 'f8e43d330f5757e076bb34e39507881d', NULL, NULL, NULL, NULL,
        'ofmachado@riobranco.ac.gov.br', '30/11/2007', 101, NULL, NULL);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (179, 'Priscila da Silva Melo', 'Acessora Tecnica', 'psmelo', '28276a521c20d0adbfb0ea4afe2c5aee',
        '711.243.652-49', NULL, '(68)-3211-2081', NULL, 'psmelo@riobranco.ac.gov.br', '04/08/2008', 97, NULL, NULL);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (115, 'Rosalia do Nascimento', NULL, 'rnascimento', '28276a521c20d0adbfb0ea4afe2c5aee', NULL, NULL, NULL, NULL,
        'rnascimento@riobranco.ac.gov.br', '03/12/2007', 344, NULL, NULL);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (116, 'Eurivan Menezes de Lima', NULL, 'emlima', 'a02d9a44f302b539d7e5b0979179504d', NULL, NULL, NULL, NULL,
        'emlima@riobranco.ac.gov.br', '04/12/2007', 88, NULL, NULL);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (117, 'Ricardo de Araújo Lima', NULL, 'ralima', '23c75ee7510b79502a8c25c9db49afe2', NULL, NULL, NULL, NULL,
        'ralima@riobranco.ac.gov.br', '04/12/2007', 341, NULL, NULL);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (118, 'Jorge Souza Rebouças Costa', NULL, 'jscosta', '28276a521c20d0adbfb0ea4afe2c5aee', NULL, NULL, NULL, NULL,
        'jscosta@riobranco.ac.gov.br', '04/12/2007', 221, NULL, NULL);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (119, 'Sandra aparecida Veiga', NULL, 'saveiga', '28276a521c20d0adbfb0ea4afe2c5aee', NULL, NULL, NULL, NULL,
        'saveiga@riobranco.ac.gov.br', '21/11/2011', 192, NULL, 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (120, 'Marcos Francisco L. Araujo', NULL, 'mlaraujo', '28276a521c20d0adbfb0ea4afe2c5aee', NULL, NULL, NULL, NULL,
        'mlaraujo@riobranco.ac.gov.br', '04/12/2007', 615, NULL, NULL);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (121, 'Hézio Rik Figueiredo', NULL, 'hrfigueiredo', '1f009e5f9cda380bba0edba6b49c6639', NULL, NULL, NULL, NULL,
        'hrfigueiredo@riobranco.ac.gov.br', '04/12/2007', 22, NULL, NULL);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (122, 'Adriana Valente', NULL, 'avoliveira', '28276a521c20d0adbfb0ea4afe2c5aee', NULL, NULL, NULL, NULL,
        'avoliveira@riobranco.ac.gov.br', '04/12/2007', 252, NULL, NULL);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (123, 'Henrique Anastácio', NULL, 'hlanastacio', 'e410f0e20b41ef23e44168e3b214e61b', NULL, NULL, NULL, NULL,
        'alanastacio@riobranco.ac.gov.br', '06/12/2007', 252, NULL, NULL);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (124, 'Marina Jardim', NULL, 'mjardim', '28276a521c20d0adbfb0ea4afe2c5aee', NULL, NULL, NULL, NULL,
        'mjardim@riobranco.ac.gov.br', '04/12/2007', 221, NULL, NULL);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (125, 'George Luiz Pereira Santos', NULL, 'gpsantos', '28276a521c20d0adbfb0ea4afe2c5aee', NULL, NULL, NULL, NULL,
        'gasantos@riobranco.ac.gov.br', '06/12/2007', 11, NULL, NULL);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (128, 'Dannya Katira Batista Coutinho', NULL, 'dbcoutinho', 'b0c2e53340c0bcad49ac25fc962b48a6', NULL, NULL, NULL,
        NULL, 'dbcoutinho@riobranco.ac.gov.br', '07/12/2007', 98, NULL, NULL);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (127, 'Paulo Henrique de Oliveira Silva', NULL, 'posilva', '28276a521c20d0adbfb0ea4afe2c5aee', NULL, NULL, NULL,
        NULL, 'posilva@riobranco.ac.gov.br', '07/12/2007', 113, NULL, NULL);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (129, 'Mafran Oliveira', NULL, 'maoliveirat', '28276a521c20d0adbfb0ea4afe2c5aee', NULL, NULL, NULL, NULL,
        'maoliveira@riobranco.ac.gov.br', '14/12/2007', 29, NULL, NULL);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (130, 'Gabriel Cunha Forneck', NULL, 'gcforneck', '28276a521c20d0adbfb0ea4afe2c5aee', NULL, NULL, NULL, NULL,
        'gcforneck@riobranco.ac.gov.br', '15/12/2007', 61, NULL, NULL);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (131, 'Evandro Luzia Teixeira', NULL, 'elteixeira', '28276a521c20d0adbfb0ea4afe2c5aee', NULL, NULL, NULL, NULL,
        'elteixeira@riobranco.ac.gov.br', '15/12/2007', 10, NULL, NULL);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (132, 'Luzanira Mendes de Oliveira', 'Chefe da Div. de Cemitérios', 'lomendes',
        '28276a521c20d0adbfb0ea4afe2c5aee', '21620830272', '196580', NULL, NULL, 'lomendes@riobranco.ac.gov.br',
        '20/02/2008', 208, NULL, NULL);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (133, 'Albaniza Gomes da Rocha', 'Auxiliar de Escritório', 'agrocha', '28276a521c20d0adbfb0ea4afe2c5aee',
        '21736189204', '15474', '3223-5038', NULL, 'agrocha@riobranco.ac.gov.br', '20/02/2008', 208, NULL, NULL);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (134, 'Kallas Roberto Kallas', NULL, 'krkallas', '28276a521c20d0adbfb0ea4afe2c5aee', NULL, NULL, NULL, NULL,
        'kkallas@bndes.gov.br', '18/03/2008', 0, NULL, NULL);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (135, 'Lelcia Maria Monteiro de Almeida', NULL, 'lmalmeida', '28276a521c20d0adbfb0ea4afe2c5aee', NULL, NULL,
        NULL, NULL, 'lmalmeida@riobranco.ac.gov.br', '27/03/2008', 93, NULL, NULL);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (136, 'Marcos Vinícius Simplício da Neves', NULL, 'mvneves', '28276a521c20d0adbfb0ea4afe2c5aee', NULL, NULL,
        NULL, NULL, 'mvneves@riobranco.ac.gov.br', '27/03/2008', 275, NULL, NULL);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (137, 'Antonio de Sousa Brito Filho', NULL, 'asbrito', '28276a521c20d0adbfb0ea4afe2c5aee', NULL, NULL, NULL,
        NULL, 'asbrito@riobranco.ac.gov.br', '22/02/2008', 97, NULL, NULL);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (138, 'Estefânia Pontes', NULL, 'epontes', '28276a521c20d0adbfb0ea4afe2c5aee', NULL, NULL, NULL, NULL,
        'epontes@riobranco.ac.gov.br', '14/07/2010', 97, NULL, 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (139, 'Sergio Roberto Lopes', NULL, 'srlopes', '28276a521c20d0adbfb0ea4afe2c5aee', NULL, NULL, NULL, NULL,
        'srlopes@riobranco.ac.gov.br', '22/02/2008', 98, NULL, NULL);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (140, 'Arthur Cézar Pinheiro Leite', NULL, 'acleite', '28276a521c20d0adbfb0ea4afe2c5aee', NULL, NULL, NULL, NULL,
        'acleite@riobranco.ac.gov.br', '22/02/2008', 98, NULL, NULL);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (141, 'Francisco Costa de Oliveira', NULL, 'sememail', '28276a521c20d0adbfb0ea4afe2c5aee', NULL, NULL, NULL,
        NULL, 'sememail@riobranco.ac.gov.br', '22/02/2008', 22, NULL, NULL);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (142, 'Fabiana Rocha Campelo', NULL, 'frcampelo', '28276a521c20d0adbfb0ea4afe2c5aee', NULL, NULL, NULL, NULL,
        'frcampelo@riobranco.ac.gov.br', '22/02/2008', 100, NULL, NULL);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (143, 'Gildo César Rocha Pinto', NULL, 'gcpinto', '28276a521c20d0adbfb0ea4afe2c5aee', NULL, NULL, NULL, NULL,
        'gcpinto@riobranco.ac.gov.br', '22/02/2008', 22, NULL, NULL);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (144, 'Ítalo César Soares Medeiros', NULL, 'icmedeiros', '28276a521c20d0adbfb0ea4afe2c5aee', NULL, NULL, NULL,
        NULL, 'icmedeiros@riobranco.ac.gov.br', '22/02/2008', 72, NULL, NULL);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (145, 'Marco Lourenço Bezerra da Silva', NULL, 'mlsilva', '28276a521c20d0adbfb0ea4afe2c5aee', NULL, NULL, NULL,
        NULL, 'mlsilva@riobranco.ac.gov.br', '22/02/2008', 74, NULL, NULL);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (146, 'Ricardo Tadeu Lopes Torres', NULL, 'rtorres', '28276a521c20d0adbfb0ea4afe2c5aee', NULL, NULL, NULL, NULL,
        'rtorres@riobranco.ac.gov.br', '22/02/2008', 72, NULL, NULL);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (147, 'Carlos Alberto Nunes Callado', NULL, 'cncallado', '28276a521c20d0adbfb0ea4afe2c5aee', NULL, NULL, NULL,
        NULL, 'cncallado@riobranco.ac.gov.br', '22/02/2008', 22, NULL, NULL);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (148, 'José Carlos Silva Fernandes', NULL, 'jfernandes', '28276a521c20d0adbfb0ea4afe2c5aee', NULL, NULL, NULL,
        NULL, 'jfernandes@riobranco.ac.gov.br', '22/02/2008', 22, NULL, NULL);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (149, 'Eluzimar Alencar de Almeida', NULL, 'eaalmeida', '28276a521c20d0adbfb0ea4afe2c5aee', NULL, NULL, NULL,
        NULL, 'eaalmeida@riobranco.ac.gov.br', '22/02/2008', 110, NULL, NULL);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (150, 'Wilson Viana Gomes Junior', NULL, 'wgjunior', '28276a521c20d0adbfb0ea4afe2c5aee', NULL, NULL, NULL, NULL,
        'wgjunior@riobranco.ac.gov.br', '14/12/2009', 21, NULL, 0);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (151, 'Amilton Fernandes Alvarenga', NULL, 'afalvarenga', '28276a521c20d0adbfb0ea4afe2c5aee', NULL, NULL, NULL,
        NULL, 'afalvarenga@riobranco.ac.gov.br', '14/12/2009', 21, NULL, 0);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (152, 'Semy Alves Ferraz', NULL, 'saferraz', '28276a521c20d0adbfb0ea4afe2c5aee', NULL, NULL, NULL, NULL,
        'saferraz@riobranco.ac.gov.br', '22/02/2008', 21, NULL, NULL);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (153, 'Francisco das Chagas Muniz Ribeiro', NULL, 'fmribeiro', '28276a521c20d0adbfb0ea4afe2c5aee', NULL, NULL,
        NULL, NULL, 'fmribeiro@riobranco.ac.gov.br', '21/03/2019', 645, NULL, 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (154, 'Wolvenar Camargo Filho', NULL, 'wcamargo', '28276a521c20d0adbfb0ea4afe2c5aee', NULL, NULL, NULL, NULL,
        'wcamargo@riobranco.ac.gov.br', '22/02/2008', 96, NULL, NULL);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (155, 'Geraldo Pereira Maia Filho', NULL, 'gpmaia', '28276a521c20d0adbfb0ea4afe2c5aee', NULL, NULL, NULL, NULL,
        'gpmaia@riobranco.ac.gov.br', '22/02/2008', 99, NULL, NULL);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (156, 'Maria das Dores Miranda de Lima', NULL, 'dmlima', '28276a521c20d0adbfb0ea4afe2c5aee', NULL, NULL, NULL,
        NULL, 'dmlima@riobranco.ac.gov.br', '22/02/2008', 2, NULL, NULL);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (157, 'Maria das Dores Araújo de Sousa', NULL, 'masousa', '28276a521c20d0adbfb0ea4afe2c5aee', NULL, NULL, NULL,
        NULL, 'masousa@riobranco.ac.gov.br', '22/02/2008', 187, NULL, NULL);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (158, 'Michel Ribeiro Paes', 'Agente Administrativo', 'mrpaes', '4951431cd430c22c32498c949d5e070c',
        '80572099234', NULL, '3226-4202', NULL, 'mrpaes@riobranco.ac.gov.br', '13/03/2008', 305, NULL, NULL);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (159, 'Fidel Kennedy de Souza Oliveira', 'Estagiário', 'fsoliveira', '28276a521c20d0adbfb0ea4afe2c5aee',
        '79002625200', NULL, '3212-7102', '8404-3403', 'fsoliveira@riobranco.ac.gov.br', '27/03/2008', 71, NULL, NULL);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (160, 'Anderson Ferreira de Lima', 'Estagiário', 'aflima', '28276a521c20d0adbfb0ea4afe2c5aee', '84687908268',
        NULL, '3212-7102', '9208-4955', 'aflima@riobranco.ac.gov.br', '27/03/2008', 71, NULL, NULL);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (161, 'Célio Roberto M. da Costa', 'Técnico de Enfermagen', 'cmcosta', '28276a521c20d0adbfb0ea4afe2c5aee',
        '41245644220', NULL, '32264402', NULL, 'cmcosta@riobranco.ac.gov.br', '28/03/2008', 305, NULL, NULL);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (162, 'Rodolfo Teles de Matos', 'Agente Administrativo', 'rtmatos', '28276a521c20d0adbfb0ea4afe2c5aee',
        '7968108268', '104434', NULL, NULL, 'rtmatos@riobranco.ac.gov.br', '28/03/2008', 305, NULL, NULL);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (163, 'James Feitosa de Araújo', 'ECONOMISTA', 'jfaraujo', '28276a521c20d0adbfb0ea4afe2c5aee', '67816428200',
        '701687', '3212-7150', NULL, 'jfaraujo@riobranco.ac.gov.br', '09/04/2008', 99, NULL, NULL);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (164, 'João Marcos Laurentino Maia', 'Assistente Administrativo', 'jmmaia', '28276a521c20d0adbfb0ea4afe2c5aee',
        '52007448220', '200273', '32280554', NULL, 'jmmaia@riobranco.ac.gov.br', '30/04/2008', 313, NULL, NULL);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (165, 'Maria Helena Batista da Silva', NULL, 'mhsilva', '28276a521c20d0adbfb0ea4afe2c5aee', '30794501249',
        '311570', NULL, NULL, 'mhsilva@riobranco.ac.gov.br', '30/04/2008', 313, NULL, NULL);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (166, 'Weima Kedila de Souza Barbosa', 'Técnico em Gestão', 'wsbarbosa', '78ee50de2a918c3f68b76d54449a00d7',
        '790.609.002-00', '701729', NULL, '(68)-9977-', 'weima-souza@bol.com.br', '25/07/2008', 97, NULL, NULL);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (167, 'Aline Dantas de Oliveira', 'Chefe do Arquivo', 'adoliveira', '28276a521c20d0adbfb0ea4afe2c5aee',
        '780.301.532-87', NULL, NULL, '(68)-9961-', 'adantas.oliveira@bol.com.br', '08/07/2008', 97, NULL, NULL);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (168, 'Semírames Maria Plácido Dias', 'Técnico em Gestão', 'spdias', '28276a521c20d0adbfb0ea4afe2c5aee',
        '813.576.992-04', '721701', NULL, '(68)-9998-', 'semiramesdias@bol.com.br', '25/07/2008', 97, NULL, NULL);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (169, 'Kelly Cristina da Silva Pinheiro', 'Chefe do Atendimento', 'kspinheiro',
        '28276a521c20d0adbfb0ea4afe2c5aee', '691.245.902-15', NULL, NULL, '(68)-9204-', 'kd.pinheiro@bol.com.br',
        '20/09/2013', 97, 'kelly.cristina@riobranco.ac.gov.br', 0);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (170, 'Jakson Pereira Cunha', 'Técnico em Gestão', 'jcpereira', '53ca4aa2671fa667f90ca763a496f6db',
        '651.977.202-04', '54493302', NULL, NULL, 'p.cunha@ibest.com.br', '24/07/2008', 97, NULL, NULL);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (171, 'Evaristo de Souza Lima', 'Técnico de Gestão', 'eslima', '28276a521c20d0adbfb0ea4afe2c5aee',
        '169.189.673-04', '701723', '(68)-3211-2080', NULL, 'sistemas@riobranco.ac.gov.br', '08/07/2008', 97, NULL,
        NULL);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (172, 'Flavio Miranda de Farias', 'Estagiario', 'fmfarias', '54878c5a5aab00dc625ed6573934b4c0', '522.886.842-91',
        NULL, '(68)-3212-7014', NULL, 'fmfarias@riobranco.ac.gov.br', '23/07/2008', 29, NULL, NULL);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (173, 'Ana Paula Azevedo Barros', 'Agente Administrativo', 'aabarros', '28276a521c20d0adbfb0ea4afe2c5aee',
        '766.832.732-00', '701819', '(68)-3211-2081', '(68)-9979-', 'aabarros@riobranco.ac.gov.br', '24/07/2008', 97,
        NULL, NULL);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (174, 'Hellen Aline de Araújo Fidelio', 'Estagiario', 'hafidelis', '28276a521c20d0adbfb0ea4afe2c5aee',
        '916.080.662-15', NULL, '(68)-3211-2081', '(68)-9282-', 'hafidelis@riobranco.ac.gov.br', '24/07/2008', 97, NULL,
        NULL);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (175, 'Maria Jossilene Dantas Benvindo', 'A. Escritorio', 'mdbenvindo', '28276a521c20d0adbfb0ea4afe2c5aee', NULL,
        NULL, NULL, NULL, 'mdbenvindo@riobranco.ac.gov.br', '25/07/2008', 4, NULL, NULL);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (176, 'Jacob Pereira Cunha', 'Tecnico em Informatica', 'jpcunha', '28276a521c20d0adbfb0ea4afe2c5aee',
        '651.976.662-34', NULL, NULL, NULL, 'jpcunha@riobranco.ac.gov.br', '25/07/2008', 4, NULL, NULL);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (177, 'João Pinho de Oliveira', 'Chefe da Seção de Desenvolvimento', 'jpoliveira',
        '28276a521c20d0adbfb0ea4afe2c5aee', '101.320.977-02', NULL, NULL, NULL, 'jpoliveira@riobranco.ac.gov.br',
        '27/01/2011', 67, NULL, 0);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (100, 'Regiani Cristina de Oliveira', 'Chefe de Divisão', 'rcoliveira', '28276a521c20d0adbfb0ea4afe2c5aee',
        '754.689.132-91', '54490804', '(68)-3211-2452', NULL, 'rcoliveira@riobranco.ac.gov.br', '04/08/2008', 97, NULL,
        NULL);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (178, 'Fabiana Pontes de Albuquerque', 'Sociólogo', 'fpalbuquerque', '28276a521c20d0adbfb0ea4afe2c5aee',
        '817.580.903-59', '701796', '(68)-3211-2452', NULL, 'fpalbuquerque@riobranco.ac.gov.br', '04/08/2008', 97, NULL,
        NULL);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (180, 'Reinildes Batista Coutinho', 'Tercerizado', 'rbcoutinho', '28276a521c20d0adbfb0ea4afe2c5aee', NULL, NULL,
        '(68)-3225-5513', NULL, 'rbcoutinho@riobranco.ac.gov.br', '18/09/2008', 216, NULL, NULL);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (181, 'Daniela Rodrigues de Albuquerque', 'Tercerizado', 'darodrigues', '28276a521c20d0adbfb0ea4afe2c5aee', NULL,
        NULL, '(68)-3223-5038', NULL, 'darodrigues@riobranco.ac.gov.br', '18/09/2008', 216, NULL, NULL);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (182, 'Geisa Bandeira de Morais', 'Analista de Sistemas', 'gbmorais', 'e654f2f412fa91718232c846f1d399eb', NULL,
        NULL, '(68)-3312-7050', NULL, 'gbmorais@riobranco.ac.gov.br', '24/09/2008', 68, NULL, NULL);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (183, 'Mayara de Souza Galdino', NULL, 'msgaldino', '967eee8c9285b60cdfc0c938e897f535', '522.153.262-04', NULL,
        NULL, NULL, 'msgaldino@riobranco.ac.gov.br', '28/10/2008', 219, NULL, NULL);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (315, 'Maria Beatriz de Simone Camargo Gurgel Fernandes', 'Coordenadora de Financiamento', 'mbfernandes',
        '28276a521c20d0adbfb0ea4afe2c5aee', '395.761.246-20', NULL, '(68)-3224-0899', NULL, 'brancamae60@bol.com.br',
        '20/09/2013', 93, 'maria.fernandes@riobranco.ac.gov.br', 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (185, 'Fábio André Souto Suárez Ortiz', 'Chefe da Seção', 'faortiz', 'ead22911c5453a0891c8de094ea77845',
        '529.711.122-68', '703492', '(68)-3226-3136', '(68)-8422-5442', 'faortiz@riobranco.ac.gov.br', '31/08/2012', 67,
        NULL, 0);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (186, 'Benedita Teste', 'teste', 'benedita', '28276a521c20d0adbfb0ea4afe2c5aee', '647.923.662-91', NULL, NULL,
        NULL, 'bdoliveira@riobranco.ac.gov.br', '23/03/2009', 28, NULL, NULL);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (187, 'Kátia Mary Pellin', 'Assessira de Planejamento', 'kmpellin', '28276a521c20d0adbfb0ea4afe2c5aee',
        '402.478.560-53', NULL, NULL, NULL, 'kmpellin@riobranco.ac.gov.br', '13/04/2009', 442, NULL, NULL);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (188, 'Milano Lucas de Moraes Evangelista', 'Chefe de Divisão', 'mmevangelista',
        'af07e46299656b47b88f1164e50eacb5', '672.297.512-68', NULL, NULL, NULL, 'mmevangelista@riobranco.ac.gov.br',
        '13/04/2009', 97, NULL, NULL);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (189, 'Mário Vitor Salgueiro da Silva', NULL, 'mvsilva', 'ee7836a8cc9c79aa3086b9f2ebf687b1', NULL, NULL, NULL,
        NULL, 'mvsilva@riobranco.ac.gov.br', '29/05/2009', 68, NULL, NULL);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (190, 'IVANILDO OLIVEIRA NEVES', 'ASSISTENTE ADMINISTRATIVO', 'IONEVES', '79fcd61b6be5411f7ca13e048e0026c6',
        '701.954.632-34', NULL, '(68)-3244-1779', NULL, 'ioneves@riobranco.ac.gov.br', '27/05/2009', 227, NULL, NULL);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (191, 'ABINE BERNARDO RODRIGUES', NULL, 'abrodrigues', '28276a521c20d0adbfb0ea4afe2c5aee', '626.831.282-15',
        NULL, NULL, '(68)-8403-', 'abrodrigues@riobranco.ac.gov.br', '27/05/2009', 227, NULL, NULL);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (192, 'DALCICLÉIA ALVES DA COSTA', 'ASSISTENTE ADMINISTRATIVA', 'dacosta', '9c586b1bd1e2d8d9fd367ffce688fc18',
        '855.806.862-34', NULL, '(68)-3244-1779', NULL, 'dacosta@riobranco.ac.gov.br', '27/05/2009', 227, NULL, NULL);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (195, 'MARIANE CRISTINE LUCENA BANDEIRA', 'AUX. TECNICO', 'mcbandeira', '28276a521c20d0adbfb0ea4afe2c5aee',
        '964.194.742-72', NULL, NULL, NULL, 'mcbandeira@riobranco.ac.gov.br', '08/06/2009', 634, NULL, NULL);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (196, 'NAIR DE OLIVEIRA BARBOSA', 'AUX. TECNICO', 'nobarbosa', '28276a521c20d0adbfb0ea4afe2c5aee',
        '884.421.012-68', NULL, '(68)-3211-2080', NULL, 'nobarbosa@riobranco.ac.gov.br', '08/06/2009', 634, NULL, NULL);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (197, 'ADILENE FERREIRA DINIZ', 'AUX. TECNICO', 'afdiniz', '28276a521c20d0adbfb0ea4afe2c5aee', '748.134.982-72',
        NULL, '(68)-3211-2080', NULL, 'afdiniz@riobranco.ac.gov.br', '08/06/2009', 634, NULL, NULL);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (198, 'NÊMORA AMÉLIA DA COSTA', 'AUX. TECNICO', 'nacosta', '28276a521c20d0adbfb0ea4afe2c5aee', '817.421.692-87',
        NULL, '(68)-3211-2080', NULL, 'nacosta@riobranco.ac.gov.br', '08/06/2009', 634, NULL, NULL);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (200, 'ZAQUEU DA SILVA ROCHA', 'AUX. ADMINISTRATIVO', 'zsrocha', '28276a521c20d0adbfb0ea4afe2c5aee',
        '776.430.382-87', '545350', '(65)-3211-2103', NULL, 'zsrocha@riobranco.ac.gov.br', '19/08/2009', 320, NULL,
        NULL);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (201, 'Silvinha Teixeira Duarte', 'Analista de Suporte', 'stduarte', '28276a521c20d0adbfb0ea4afe2c5aee',
        '360.523.022-91', NULL, '(68)-3211-2250', NULL, 'stduarte@riobranco.ac.gov.br', '26/08/2009', 65, NULL, NULL);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (202, 'João 2', NULL, 'pinhojoao', '28276a521c20d0adbfb0ea4afe2c5aee', NULL, NULL, NULL, NULL,
        'pinhojoao@riobranco.ac.gov.br', '05/11/2009', 4, 'pinhojoao@gmail.com', 0);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (203, 'Gardênia Oliveira Sales', 'Ger. Elaboração de Instrumentos de Planejamento', 'gosales',
        '28276a521c20d0adbfb0ea4afe2c5aee', NULL, NULL, NULL, NULL, 'gosales@riobranco.ac.gov.br', '16/11/2009', 2,
        'gardenia_cta@yahoo.com.br', 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (204, 'Daniel Moura Damasceno Netto', 'Assistente administrativo', 'dmnetto', '28276a521c20d0adbfb0ea4afe2c5aee',
        '112.913.622-15', '4081-01', '(06)-3212-7102', NULL, 'dmnetto@riobranco.ac.gov.br', '14/09/2017', 71, NULL, 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (205, 'Usuário de atendimento ACGOVERNE', 'prestador de serviço', 'chamados', '28276a521c20d0adbfb0ea4afe2c5aee',
        NULL, NULL, NULL, NULL, 'chamados@acgoverne.com.br', '22/12/2010', 4, NULL, 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (206, 'Iara de Oliveira Marques Parrilha', 'chefe', 'iomarques', '28276a521c20d0adbfb0ea4afe2c5aee',
        '619.587.942-87', '545479', '(68)-3211-2432', NULL, 'iomarques@riobranco.ac.gov.br', '10/01/2013', 437, NULL,
        0);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (207, 'Maria do Socorro Lopes de Lima', 'comissão', 'mllopes', '28276a521c20d0adbfb0ea4afe2c5aee',
        '391.398.162-49', '70210602', '(68)-3211-2432', NULL, 'mllopes@riobranco.ac.gov.br', '11/12/2009', 437, NULL,
        1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (208, 'Nádia V. Pereira', 'Coordenador Técnico', 'nvpereira', '28276a521c20d0adbfb0ea4afe2c5aee',
        '840.452.056-91', NULL, '(68)-3211-2246', NULL, 'nvpereira@riobranco.ac.gov.br', '21/12/2009', 248,
        'nadia.pereira@gmail.com', 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (209, 'Maria das Graças Lopes de Castro', 'Gerente Casa Rosa Mulher', 'glcastro',
        '28276a521c20d0adbfb0ea4afe2c5aee', '196.942.882-15', '544959-1', '(68)-3223-5492', '(68)-9971-',
        'glcastro@riobranco.ac.gov.br', '04/01/2010', 52, NULL, 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (210, 'Rosali Scalabrin', 'Coordenadora', 'rscalabrin', '28276a521c20d0adbfb0ea4afe2c5aee', '134.539.042-49',
        NULL, '(68)-3211-2433', NULL, 'rscalabrin@riobranco.ac.gov.br', '04/01/2010', 52, NULL, 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (211, 'Elisângela Maria Pontes de Souza', 'gerente', 'epsouza', '28276a521c20d0adbfb0ea4afe2c5aee',
        '560.673.482-68', NULL, '(68)-3211-2228', NULL, 'epsouza@riorbanco.ac.gov.br', '04/01/2010', 52,
        'elipontes13@hotmail.com', 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (212, 'Sâmea Brito de França', 'gerente', 'sbfranca', '28276a521c20d0adbfb0ea4afe2c5aee', '308.741.652-53',
        '702405-02', '(68)-3211-2433', NULL, 'sbfranca@riobranco.ac.gov.br', '04/01/2010', 52, 'sameabrit@gmail.com',
        1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (213, 'Olicino Nascimento Duarte', 'Assessor de comunicação', 'olduarte', '28276a521c20d0adbfb0ea4afe2c5aee',
        '671.911.699-15', '702434', '(68)-3211-2213', NULL, 'olduarte@riobranco.ac.gov.br', '04/01/2010', 49,
        'bsbduarte@gmail.com', 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (214, 'Pascal Abou Khalil', 'Secretario', 'pakhalil', '28276a521c20d0adbfb0ea4afe2c5aee', '196.497.622-72', NULL,
        NULL, NULL, 'pakhalil@riobranco.ac.gov.br', '04/01/2010', 95, NULL, 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (215, 'Gleizer Rodrigues de lima', '...', 'glrodrigues', '28276a521c20d0adbfb0ea4afe2c5aee', '412.272.712-04',
        NULL, '(68)-2106-8017', NULL, 'glrodrigues@riobranco.ac.gov.br', '04/01/2010', 96, NULL, 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (216, 'Arthur de Oliveira Viana Neto', 'diretor', 'avneto', '28276a521c20d0adbfb0ea4afe2c5aee', NULL, NULL,
        '(68)-2106-8022', NULL, 'avneto@riobranco.ac.gov.br', '04/01/2010', 96, NULL, 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (217, 'Fabiana Raggi de Sá Santana', 'diretora', 'fraggi', '28276a521c20d0adbfb0ea4afe2c5aee', '023.657.816-24',
        NULL, '(68)-2106-8028', NULL, 'fraggi@riobranco.ac.gov.br', '04/01/2010', 96, NULL, 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (218, 'Juliana Bicalho Messeder  Castro', 'arquiteta', 'jbcastro', '28276a521c20d0adbfb0ea4afe2c5aee',
        '914.152.336-91', NULL, '(68)-2106-8031', NULL, 'jbcastro@riobranco.ac.gov.br', '04/01/2010', 96, NULL, 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (219, 'Marinelsi Rossi', 'ass. tecnica', 'mrossi', '28276a521c20d0adbfb0ea4afe2c5aee', '053.294.692-87', NULL,
        '(68)-3211-2270', NULL, 'mrossi@riobranco.ac.gov.br', '04/01/2010', 10, 'rossi.marinelsi@gmail.com', 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (220, 'Raimundo Angelim Vasconcelos', 'Prefeito', 'prefeito', '28276a521c20d0adbfb0ea4afe2c5aee', NULL, NULL,
        '(68)-3211-2202', NULL, 'prefeito@riobranco.ac.gov.br', '10/01/2013', 10, NULL, 0);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (221, 'Zeli Isabel Ambrós', 'chefe de gabinete', 'zambros', '28276a521c20d0adbfb0ea4afe2c5aee', '255.829.500-44',
        NULL, '(68)-3211-2202', NULL, 'zambros@iobranco.ac.gov.br', '10/01/2013', 10, NULL, 0);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (222, 'Andréia de Oliveira', 'gerente', 'aoliveira', '28276a521c20d0adbfb0ea4afe2c5aee', NULL, NULL, NULL, NULL,
        'aoliveira@riobranco.ac.gov.br', '04/01/2010', 72, NULL, 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (223, 'Aline Louise Silva Ramos', 'coordenadora', 'asramos', '28276a521c20d0adbfb0ea4afe2c5aee', NULL, NULL,
        '(68)-3228-4995', NULL, 'asramos@riobranco.ac.gov.br', '04/01/2010', 72, NULL, 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (224, 'Núbia Bessa de Araujo', 'diretora', 'nbaraujo', '28276a521c20d0adbfb0ea4afe2c5aee', NULL, NULL,
        '(68)-3228-4995', NULL, 'nbaraujo@riobranco.ac.gov.br', '04/01/2010', 72, NULL, 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (225, 'Soraia das Chagas Leite', 'che fe de gabinete', 'scleite', '28276a521c20d0adbfb0ea4afe2c5aee', NULL, NULL,
        '(68)-3228-8199', NULL, 'scleite@riobranco.ac.gov.br', '04/01/2010', 72, NULL, 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (226, 'Nélio Anastácio de Oliveira', 'diretor', 'naoliveira', '28276a521c20d0adbfb0ea4afe2c5aee', NULL, NULL,
        '(68)-3228-4995', NULL, 'naoliveira@riobranco.ac.gov.br', '04/01/2010', 72, NULL, 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (227, 'Cleiciany da Silva Lima', 'gerente', 'cslima', '28276a521c20d0adbfb0ea4afe2c5aee', NULL, NULL,
        '(68)-3228-4995', NULL, 'cslima@riobranco.ac.gov.br', '04/01/2010', 72, NULL, 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (228, 'Tony Marle Amorim Areal', 'chefe', 'tmarreal', '28276a521c20d0adbfb0ea4afe2c5aee', NULL, NULL,
        '(68)-3228-4995', NULL, 'tmarreal@riobranco.ac.gov.br', '04/01/2010', 72, NULL, 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (229, 'José Otávio Francisco Parreira', 'secretario', 'joparreira', '28276a521c20d0adbfb0ea4afe2c5aee',
        '034.170.468-74', '700284', '(68)-3212-7098', NULL, 'joparreira@riobranco.ac.gov.br', '04/01/2010', 645,
        'joseofp@gmail.com', 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (230, 'Lilian Kristina Sales Amim', 'chefe de gabinete', 'lkamim', '28276a521c20d0adbfb0ea4afe2c5aee',
        '678.224.602-34', '544960-1', '(68)-3212-7098', NULL, 'lkamim@riobranco.ac.gov.br', '04/01/2010', 645,
        'liliamim@gmail.com', 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (231, 'Maria Bernadete Mendes de Oliveira', 'chefe', 'mboliveira', '28276a521c20d0adbfb0ea4afe2c5aee',
        '079.399.062-91', NULL, '(68)-3211-246', NULL, 'mboliveira@riobranco.ac.gov.br', '05/01/2010', 97, NULL, 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (232, 'Rejane Medeiros de Souza', 'chefe', 'rsmedeiros', '28276a521c20d0adbfb0ea4afe2c5aee', '579.195.772-68',
        NULL, '(68)-3211-2471', NULL, 'rsmedeiros@riobranco.ac.gov.br', '05/01/2010', 97, NULL, 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (233, 'Neyva Janara Rocha de Carvalho', 'coordenadora', 'njcarvalho', '28276a521c20d0adbfb0ea4afe2c5aee',
        '444.063.552-00', NULL, '(68)-3223-6768', NULL, 'njcarvalho@riobranco.ac.gov.br', '05/01/2010', 97, NULL, 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (237, 'Elisângela da Silva Andrade Santos', 'Assessora', 'esandrade', '28276a521c20d0adbfb0ea4afe2c5aee',
        '617.481.022-49', NULL, '(68)-2106-8019', NULL, 'esandrade@riobranco.ac.gov.br', '05/01/2010', 96, NULL, 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (235, 'Paulo Henrique de Oliveira Araujo', 'chefe', 'poaraujo', '28276a521c20d0adbfb0ea4afe2c5aee',
        '592.098.012-53', NULL, '(68)-3211-2474', NULL, 'paulo.araujo@riobranco.ac.gov.br', '20/09/2013', 97, NULL, 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (236, 'Fábio Fabricio Pereira da Silva', 'diretor', 'ffsilva', '28276a521c20d0adbfb0ea4afe2c5aee',
        '870.467.172-49', NULL, '(68)-3211-2459', NULL, 'ffsilva@riobranco.ac.gov.br', '05/01/2010', 97, NULL, 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (238, 'Antonio Euzebio Pinheiro', 'Diretor', 'aepinheiro', '28276a521c20d0adbfb0ea4afe2c5aee', '138.091.482-53',
        NULL, '(68)-3211-2225', NULL, 'aepinheiro@riobranco.ac.gov.br', '05/01/2010', 3, NULL, 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (239, 'Josué da Silva Santos', 'chefe', 'jssantos', '28276a521c20d0adbfb0ea4afe2c5aee', '830.407.732-91', NULL,
        '(68)-3211-2209', NULL, 'jssantos@riobranco.ac.gov.br', '05/01/2010', 3, NULL, 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (240, 'João Paulo Alves Mendes', 'chefe', 'jamendes', '28276a521c20d0adbfb0ea4afe2c5aee', '782.248.962-34',
        '702844-1', '((6)-8312-7709', NULL, 'jamendes@riobranco.ac.gov.br', '06/01/2010', 645, NULL, 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (241, 'Guilherme Franco Matny', 'diretor', 'gfmatny', '28276a521c20d0adbfb0ea4afe2c5aee', '308.441.772-53',
        '5388890-2', '(68)-3212-7098', NULL, 'gfmatny@riobranco.ac.gov.br', '06/01/2010', 645, NULL, 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (242, 'Márcio José Batista', 'Secretario', 'mjbatista', '28276a521c20d0adbfb0ea4afe2c5aee', '411.670.582-91',
        '702489', '(68)-3211-2227', NULL, 'mjbatista@riobranco.ac.gov.br', '06/01/2010', 9, NULL, 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (243, 'Paulo Sérgio Braña Muniz', 'Coordenador', 'pbmuniz', '28276a521c20d0adbfb0ea4afe2c5aee', '161.298.572-68',
        NULL, '(68)-3211-2239', NULL, 'pbmuniz@riobranco.ac.gov.br', '06/01/2010', 12, NULL, 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (244, 'Mirna Pinheiro Caniso', 'Gerente de Educação e Economia Solidária', 'mpcaniso',
        '28276a521c20d0adbfb0ea4afe2c5aee', '618.980.402-06', NULL, '(68)-3211-2239', NULL,
        'mpcaniso@riobranco.ac.gov.br', '06/01/2010', 12, 'mirnacaniso@gmail.com', 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (245, 'James Leão de Oliveira', 'Chefe da Div. Pol. Cred.', 'jloliveira', '28276a521c20d0adbfb0ea4afe2c5aee',
        '440.274.942-53', '54483501', '(68)-3211-2224', NULL, 'jloliveira@riobranco.ac.gov.br', '06/01/2010', 12,
        'jamesleao@uol.com.br', 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (246, 'Jacqueline Mello de Souza Pinheiro', 'chefe de cerimonial', 'jpinheiro',
        '28276a521c20d0adbfb0ea4afe2c5aee', '470.082.619-34', '536628-4', '(68)-3211-2429', NULL,
        'jpinheiro@riobranco.ac.gov.br', '06/01/2010', 239, 'jackiepinheiro@gmail.com', 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (247, 'Vagno Celestino Lauro de Paula', 'Assistente cerimonial', 'vdpaula', '28276a521c20d0adbfb0ea4afe2c5aee',
        '197.163.382-87', '545481-3', '(68)-3211-2429', NULL, 'vdpaula@riobranco.ac.gov.br', '06/01/2010', 239,
        'vagnodipaula@gmail.com', 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (248, 'Márcia da Costa Oliveira', 'Diretora', 'cmoliveira', '28276a521c20d0adbfb0ea4afe2c5aee', '621.129.452-20',
        '701285-3', '(68)-3228-5765', NULL, 'cmoliveira@riobranco.ac.gov.br', '07/01/2010', 98, NULL, 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (249, 'Francisco Anastácio Cezário Braga', 'Secretário', 'fabraga', '28276a521c20d0adbfb0ea4afe2c5aee',
        '182.989.232-00', '702493', '(68)-3225-5513', NULL, 'fabraga@riobranco.av.gov.br', '11/01/2010', 100, NULL, 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (250, 'Frankcinato da Silva Batista', 'Chefe de Gabinete', 'fbatista', '28276a521c20d0adbfb0ea4afe2c5aee',
        '340.361.132-91', '5455473', '(68)-3211-2220', NULL, 'fbatista@riobranco.ac.gov.br', '11/01/2010', 45, NULL, 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (251, 'Antonia Irismar de Lima Souza Oliveira', 'Secretária Administrativa', 'airismar',
        '28276a521c20d0adbfb0ea4afe2c5aee', '339.689.692-00', '5454273', '(68)-3211-2220', NULL,
        'airismar@riobranco.ac.gov.br', '11/01/2010', 45, NULL, 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (252, 'Gilvan de Oliveira Vasconcelos', 'Assistente Militar', 'goliveira', '28276a521c20d0adbfb0ea4afe2c5aee',
        '028.196.272-34', '545412', '(68)-3211-2430', NULL, 'goliveira@riobranco.ac.gov.br', '12/01/2010', 11, NULL, 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (253, 'Adinete Casseb Braga Souza', 'Chefe Div. de Rede Ens. Fund.', 'absouza',
        '28276a521c20d0adbfb0ea4afe2c5aee', '301.190.572-87', '175311', '(68)-3211-2445', NULL,
        'absouza@riobranco.ac.gov.br', '12/01/2010', 88, NULL, 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (254, 'Gleide Maria Gonçalves de Souza', 'Professora', 'gmgsouza', '28276a521c20d0adbfb0ea4afe2c5aee',
        '308.365.302-68', '3621470', '(68)-3211-2445', NULL, 'gmgsouza@riobranco.ac.gov.br', '12/01/2010', 88,
        'gleyde@superig.com.br', 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (255, 'Maria da Conceição Borges de Lima', 'Diretora de Gestão', 'mblima', '28276a521c20d0adbfb0ea4afe2c5aee',
        '339.338.202-00', '559561', '(68)-3211-2446', NULL, 'mblima@riobranco.ac.gov.br', '12/01/2010', 88, NULL, 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (256, 'Maria Eliana Pontes de Oliveira', 'Técnico', 'meoliveira', '28276a521c20d0adbfb0ea4afe2c5aee',
        '037.711.482-00', '14698601', '(68)-3211-2437', NULL, 'meoliveira@riobranco.ac.gov.br', '12/01/2010', 88, NULL,
        1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (257, 'Ligia Ferreira Ribeiro', 'Diretora de Ensino', 'lfribeiro', '28276a521c20d0adbfb0ea4afe2c5aee',
        '151.753.671-53', '178841', '(68)-3211-2449', NULL, 'lfribeiro@riobranco.ac.gov.br', '12/01/2010', 88, NULL, 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (258, 'Adelaide Maria Costa Silva', 'Técnica do Ensino Fundamental', 'acsilva',
        '28276a521c20d0adbfb0ea4afe2c5aee', '015.285.302-25', '98221', '(68)-3211-2437', NULL,
        'acsilva@riobranco.ac.gov.br', '12/01/2010', 88, NULL, 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (259, 'Cláudia Rosário Silva Mesquita', 'Professora', 'crmesquita', '28276a521c20d0adbfb0ea4afe2c5aee',
        '321.856.262-72', '23091001', '(68)-3211-2486', NULL, 'crmesquita@riobranco.ac.gov.br', '12/01/2010', 88,
        'claudiarosario3@gmail.com', 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (260, 'Raheth Casseb Braga Borges', 'Assessora de Planejamento', 'rbborges', '28276a521c20d0adbfb0ea4afe2c5aee',
        '307.792.862-00', NULL, '(68)-3211-2411', NULL, 'rbborges@riobranco.ac.gov.br', '12/01/2010', 88, NULL, 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (261, 'Maria Jeanett de Souza Esteves', 'Chefe do Setor', 'msesteves', '28276a521c20d0adbfb0ea4afe2c5aee',
        '197.567.472-34', '1830837', '(68)-3211-2413', NULL, 'msesteves@riobranco.ac.gov.br', '12/01/2010', 88, NULL,
        1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (267, 'Afrânio Moura de Lima', 'Diretor do Departamento de Desporto e Lazer', 'amlima',
        '28276a521c20d0adbfb0ea4afe2c5aee', '123.121.772-34', '192083', '(68)-3223-5202', NULL,
        'amlima@riobranco.ac.gov.br', '20/01/2010', 93, NULL, 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (266, 'Valdo Nicacio Lima', 'Diretor Administrativo e Financeiro', 'vnlima', '28276a521c20d0adbfb0ea4afe2c5aee',
        '101.320.977-02', '5382504', '(68)-3224-0899', NULL, 'vnlima@riobranco.ac.gov.br', '20/01/2010', 93, NULL, 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (264, 'Elza Neves Lopes', 'Professora', 'enlopes', '28276a521c20d0adbfb0ea4afe2c5aee', '138.651.702-00',
        '1938001', '(68)-3211-2441', NULL, 'enlopes@riobranco.ac.gov.br', '12/01/2010', 88, 'elzalopes42@yahoo.com.br',
        1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (265, 'Karine Pereira Soares', 'Professora', 'kpsoares', '28276a521c20d0adbfb0ea4afe2c5aee', '935.158.805-00',
        '5435781', '(68)-3211-2441', NULL, 'kpsoares@riobranco.ac.gov.br', '12/01/2010', 88, 'karinesaguiar@bol.com.br',
        1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (268, 'Maria Janete Souza dos Santos', 'secretária de Planejamento', 'mssantos',
        '28276a521c20d0adbfb0ea4afe2c5aee', '216.219.692-15', '7023932', '(68)-3211-2206', NULL,
        'mssantos@riobranco.ac.gov.br', '20/01/2010', 3, NULL, 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (269, 'Francisco Eduardo Saraiva de Farias', 'Vice-Prefeito', 'efarias', '28276a521c20d0adbfb0ea4afe2c5aee',
        NULL, NULL, '(68)-3211-2232', NULL, 'efarias@riobranco.ac.gov.br', '21/01/2010', 45, NULL, 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (270, 'Daisy Aparecida Pereira Gomes Silva', 'Diretora', 'dasilva', '28276a521c20d0adbfb0ea4afe2c5aee',
        '461.505.522-68', '7027221', '(68)-3228-2377', NULL, 'dasilva@riobranco.ac.gov.br', '21/01/2010', 117,
        'daisygsilva@gmail.com', 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (271, 'Luzia Bezerra da Silva Pontes', 'Auxiliar de Escritório/Gabinete', 'lbpontes',
        '28276a521c20d0adbfb0ea4afe2c5aee', '128.963.492-00', '53710', '(68)-3228-2894', NULL,
        'lbpontes@riobranco.ac.gov.br', '21/01/2010', 98, NULL, 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (272, 'Eliane de Lima e Silva', 'Comercialização em exercício', 'elsilva', '28276a521c20d0adbfb0ea4afe2c5aee',
        '196.597.682-49', '5463943', '(68)-3224-5333', NULL, 'elsilva@riobranco.ac.gov.br', '22/01/2010', 94, NULL, 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (273, 'Cláudia Alves do Valle Stehling', 'Assessora de Planejamento', 'cvstehling',
        '28276a521c20d0adbfb0ea4afe2c5aee', '000.652.336-62', '5463853', '(68)-3224-5333', NULL,
        'cvstehling@riobranco.ac.gov.br', '22/01/2010', 94, NULL, 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (274, 'Rosiane Silveira de Lima', 'Gerente', 'rslima', '28276a521c20d0adbfb0ea4afe2c5aee', '577.367.402-59',
        '54542303', NULL, NULL, 'rslima@riobranco.ac.gov.br', '25/01/2010', 12, 'rosisl2@yahoo.com.br', 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (275, 'Victor Hugo Lima de Sousa', 'Suporte', 'victor.souza', '28276a521c20d0adbfb0ea4afe2c5aee',
        '710.202.172-00', NULL, '(68)-3212-7020', NULL, 'victor.souza@riobranco.ac.gov.br', '10/01/2015', 68, NULL, 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (276, 'Iara de Oliveira Santos Andrades', 'Analista de Sistemas', 'iosantos', '1169ce2f4cee31c7f906f8d95fce1e7c',
        '841.111.942-49', '703.112-01', '(68)-3212-7014', NULL, 'iara.andrades@riobranco.ac.gov.br', '20/09/2013', 4,
        NULL, 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (277, 'Márcia Cristina Cordeiro Lopes Alódio', 'Procuradora geral', 'mlalodio',
        '28276a521c20d0adbfb0ea4afe2c5aee', NULL, '538791', '(68)-3212-7070', NULL, 'mlalodio@riobranco.ac.gov.br',
        '01/03/2010', 38, NULL, 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (278, 'José Antônio Ferreira de Souza', 'Procurador', 'jfsouza', '28276a521c20d0adbfb0ea4afe2c5aee', NULL,
        '544581', '(68)-3212-7029', NULL, 'jfsouza@riobranco.ac.gov.br', '01/03/2010', 429, NULL, 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (279, 'Sandra de Abreu Macedo', NULL, 'samacedo', '28276a521c20d0adbfb0ea4afe2c5aee', NULL, '537529',
        '(68)-3212-7088', NULL, 'samacedo@riobranco.ac.gov.br', '01/03/2010', 167, NULL, 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (280, 'Francisca Araújo da Mota', 'Procuradora geral adjunta', 'famota', '28276a521c20d0adbfb0ea4afe2c5aee',
        NULL, '544174', '(68)-3212-7029', NULL, 'famota@riobranco.ac.gov.br', '01/03/2010', 38, NULL, 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (281, 'Dimas da Silva Sandas', 'Divisão de Políticas Públicas', 'dssandas', '28276a521c20d0adbfb0ea4afe2c5aee',
        '662.213.742-00', NULL, '(68)-3211-2223', NULL, 'dssandas@riobranco.ac.gov.br', '03/03/2010', 61, NULL, 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (282, 'Oteniel Almeida dos Santos', 'Coordenador', 'oasantos', '28276a521c20d0adbfb0ea4afe2c5aee',
        '527.963.022-53', NULL, '(68)-3211-2223', NULL, 'oasantos@riobranco.ac.gov.br', '03/03/2010', 61, NULL, 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (283, 'Moacir Fecury Ferreira da Silva', 'Secretário Municipal de Educação', 'mfecury',
        '28276a521c20d0adbfb0ea4afe2c5aee', '190.587.137-68', NULL, '(68)-3211-2403', NULL,
        'mfecury@riobranco.ac.gov.br', '10/03/2010', 88, NULL, 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (284, 'Maria Genacilda da Silva Félix', 'Chefe de Divisão de Ensino Rural', 'mgfelix',
        '28276a521c20d0adbfb0ea4afe2c5aee', '411.653.062-04', NULL, '(68)-3211-2403', NULL,
        'mgfelix@riobranco.ac.gov.br', '10/03/2010', 88, NULL, 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (285, 'Francisco Evandro Rosas da Costa', 'Secretário', 'fecosta', '28276a521c20d0adbfb0ea4afe2c5aee',
        '214.050.422-49', '7012083', '(68)-3212-7005', NULL, 'fecosta@riobranco.ac.gov.br', '22/03/2010', 25, NULL, 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (286, 'Yargo Vinicius Costa de Araujo', 'Estagiario', 'ycaraujo', '28276a521c20d0adbfb0ea4afe2c5aee', NULL, NULL,
        NULL, NULL, 'ycaraujo@riobranco.ac.gov.br', '13/04/2010', 71, NULL, 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (287, 'Suellen Souza Silva', 'Técnico em Gestão Pública', 'sssuellen', '28276a521c20d0adbfb0ea4afe2c5aee',
        '779.954.652-20', '701609', '(68)-3212-7001', NULL, 'sssuellen@riobranco.ac.gov.br', '15/02/2017', 125, NULL,
        1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (288, 'Patrick Barbosa Lopes', 'Secretário Administrativo', 'pblopes', '28276a521c20d0adbfb0ea4afe2c5aee',
        '634.262.852-15', NULL, '(68)-3212-7012', NULL, 'pblopes@riobranco.ac.gov.br', '16/04/2010', 125, NULL, 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (289, 'Marlucia Silva de Souza', 'Chefe da Seção de Apoio Administrativo', 'mssouza',
        '28276a521c20d0adbfb0ea4afe2c5aee', '233.594.342-87', NULL, '(68)-3212-7012', NULL,
        'mssouza@riobranco.ac.gov.br', '16/04/2010', 125, NULL, 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (290, 'Clévia Nunes de Souza', 'Chefe de Gabinete', 'cnsouza', '28276a521c20d0adbfb0ea4afe2c5aee', NULL,
        '70037205', '(68)-3212-7012', NULL, 'cnsouza@riobranco.ac.gov.br', '16/04/2010', 125, NULL, 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (291, 'Elaine Cristinne Morais do Nascimento', 'Técnico em Gestão Pública', 'enmorais',
        '28276a521c20d0adbfb0ea4afe2c5aee', '817.692.612-49', '70236401', '(68)-3212-7043', NULL,
        'elaine.morais@riobranco.ac.gov.br', '15/02/2017', 125, NULL, 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (292, 'Lorena Costa Irmão Dias', 'Assessora de Planejamento', 'lcdias', '28276a521c20d0adbfb0ea4afe2c5aee',
        '648.633.602-15', '702771-1', '(68)-3211-2466', NULL, 'lcdias@riobranco.ac.gov.br', '14/07/2010', 269, NULL, 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (293, 'Marinete Chagas da Costa', 'Secretária', 'mmcosta', 'a9be45dc75c1a2fef5f3adb001576d16', '308.762.142-00',
        '700064', '(68)-3223-5077', NULL, 'saerbmary@gmail.com', '08/01/2014', 368,
        'marinete.costa@riobranco.ac.gov.br', 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (294, 'Rutileny Cristina de Brito Lima Bastos', 'Presidente da CPL', 'rclima',
        '28276a521c20d0adbfb0ea4afe2c5aee', '561.933.782-00', '700096', '(68)-3223-5077', NULL,
        'rutileny.bastos@riobranco.ac.gov.br', '08/11/2013', 371, NULL, 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (295, 'Laura Cristina de Paiva Melo Gonçalves', 'Chefe da Divisão de Programação Orçamentária', 'lpmelo',
        '7a451b51c1881285db014b5f8c9b80a8', '715.493.312-72', '545470-3', '(68)-3212-7039', NULL,
        'laura.goncalves@riobranco.ac.gov.br', '20/09/2013', 85, 'lauracmelo@gmail.com', 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (296, 'Maria Auxiliadora Assem Ide', 'Chefe do Gabinete', 'aaide', '28276a521c20d0adbfb0ea4afe2c5aee', NULL,
        NULL, '(68)-3211-2460', NULL, 'aaide@riobranco.ac.gov.br', '09/11/2010', 97, NULL, 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (297, 'Greice Helionay Freitas dos Passos', 'Assessora', 'ghpassos', '28276a521c20d0adbfb0ea4afe2c5aee',
        '183.213.052-52', '600010-5', '(68)-2106-8025', NULL, 'ghpassos@riobranco.ac.gov.br', '09/11/2010', 96, NULL,
        1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (298, 'Maria Geny de Oliveira Costa', 'Auxiliar Administrativa', 'mocosta', '28276a521c20d0adbfb0ea4afe2c5aee',
        '360.292.462-91', NULL, '(68)-3225-5513', NULL, 'geny.costa@riobranco.ac.gov.br', '20/09/2013', 100, NULL, 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (299, 'Tailândia Daniel da Silva', 'Auxiliar Administrativa', 'tdsilva', '28276a521c20d0adbfb0ea4afe2c5aee',
        '781.913.272-87', NULL, '(68)-3225-5513', NULL, 'tailandia.silva@riobranco.ac.gov.br', '20/09/2013', 100, NULL,
        1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (300, 'Agostinho Ferreira Lima Neto', 'Tecnologia', 'afneto', '28276a521c20d0adbfb0ea4afe2c5aee',
        '854.039.542-87', NULL, '(68)-3225-5513', NULL, 'afneto@riobranco.ac.gov.br', '10/01/2013', 100, NULL, 0);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (301, 'Ana Paula Nascimento Dankar', 'Téc. em Gestão Pública', 'apdankar', 'c9f6c796375dd4ad24960f4fb24eef6b',
        '703.828.122-91', '701718-01', '(68)-3213-2506', NULL, 'ana.dankar@riobranco.ac.gov.br', '08/01/2014', 294,
        NULL, 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (302, 'Bianca Sales Cruz', 'Divisão de Atos Oficiais', 'bscruz', '0550582e18212fccfcddd36e2fb418ad',
        '652.149.352-34', '545426-03', '(68)-3211-2230', NULL, 'atosoficiais@riobranco.ac.gov.br', '10/01/2013', 238,
        'bscruz@riobranco.ac.gov.br', 0);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (303, 'Claudete Lurdes Damo de Araújo', 'Assessora de Gestão', 'claraujo', '28276a521c20d0adbfb0ea4afe2c5aee',
        '234.281.390-20', '546406-04', '(68)-3211-2230', NULL, 'atosoficiais@riobranco.ac.gov.br', '10/01/2013', 238,
        'claraujo@riobranco.ac.gov.br', 0);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (304, 'Mara Clícia Eugênio Rosas', 'Chefe Div. Adm.', 'merosas', '6d77627da57a2579656d76816a281b28',
        '632.688.842-53', '702407-2', '(68)-3228-4995', NULL, 'mara.rosas@riobranco.ac.gov.br', '20/09/2013', 72, NULL,
        1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (305, 'Maria Auxiliadora Felismino de Azevedo', 'Secretária de Adminstração de Mercados', 'mfazevedo',
        '28276a521c20d0adbfb0ea4afe2c5aee', '217.805.792-68', '70215-2', '(68)-3244-1779', NULL,
        'maria.azevedo@riobranco.ac.gov.br', '20/09/2013', 94, NULL, 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (306, 'Hanairton Cavalcante', 'Assessor Administrativo e Financeiro', 'hcavalcante',
        '28276a521c20d0adbfb0ea4afe2c5aee', '340.057.222-53', '703493-1', '(68)-3211-4252', NULL,
        'hanairtoncavalcante@gmail.com', '10/11/2010', 645, NULL, 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (307, 'Zélia Maria dos Santos Lima Suknaic', 'Chefe de Gabinete', 'zlsuknaic',
        '28276a521c20d0adbfb0ea4afe2c5aee', '217.444.672-34', '700844-3', '(68)-3211-4252', NULL, 'zlsuknaic@gmail.com',
        '10/11/2010', 645, NULL, 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (308, 'Layla Gomes Marinho', 'Chefe de Gabinete', 'lmgomes', '28276a521c20d0adbfb0ea4afe2c5aee',
        '874.773.512-00', NULL, '(68)-3228-4995', NULL, 'layla.marinho@riobranco.ac.gov.br', '20/09/2013', 72, NULL, 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (309, 'Uiara de Almeida Jucá', 'Téc. em Gestão Pública', 'uajuca', '28276a521c20d0adbfb0ea4afe2c5aee',
        '803.456.462-15', '703155-01', '(68)-3222-8680', NULL, 'uiara.nascimento@riobranco.ac.gov.br', '08/01/2014',
        645, NULL, 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (310, 'Ana Cristina Maia Jansen', 'Chefe de Gabinete', 'acjansen', '28276a521c20d0adbfb0ea4afe2c5aee',
        '339.807.302-63', '545507-03', '(32)-2833-26', NULL, 'acjansen@riobranco.ac.gov.br', '11/11/2010', 98, NULL, 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (311, 'Edileuza Gomes dos Reis', 'Chefe da Div. Orçamentária', 'egreis', '28276a521c20d0adbfb0ea4afe2c5aee',
        '196.205.192-72', '3727', '(68)-3212-7033', NULL, 'edileuza.reis@riobranco.ac.gov.br', '20/09/2013', 38, NULL,
        1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (312, 'Priscila Maia de Souza', 'Assessora Técnica', 'pmsouza', '28276a521c20d0adbfb0ea4afe2c5aee',
        '527.885.982-20', '703223-01', '(68)-3212-7016', NULL, 'priscilamaias@gmail.com', '20/09/2013', 38,
        'priscila.souza@riobranco.ac.gov.br', 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (313, 'Ana Carolina Angelo Passos', 'Téc. em Gestão Pública', 'acangelo', 'd9270b997ffde9e45a98cf37cc8358a9',
        '930.166.702-91', '702361-1', '(68)-3211-2434', NULL, 'ana.passos@riobranco.ac.gov.br', '01/12/2014', 344, NULL,
        0);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (314, 'Alexandra Mirella Barroso Martins', 'Téc. em Gestão Pública', 'ammartins',
        '9cbe057e0dca9d61d83859364f0367e6', '825.668.362-72', '701733-01', '(68)-3211-2434', NULL,
        'mirellabarrosoac@bol.com.br', '08/01/2014', 344, 'alexandra.martins@riobranco.ac.gov.br', 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (316, 'Maria Cristina Soares Rocha', 'Gerente de Departamento', 'msrocha', '28276a521c20d0adbfb0ea4afe2c5aee',
        '308.234.392-91', '18716-1', '(68)-3212-7079', NULL, 'cristina.rocha@riobranco.ac.gov.br', '20/09/2013', 104,
        NULL, 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (317, 'Cleonice Negreiros de Oliveira', 'Gerente Administrativa', 'cnoliveira',
        '28276a521c20d0adbfb0ea4afe2c5aee', '079.389.002-06', '20745', '(68)-3212-7108', NULL,
        'cleonice.oliveira@riobranco.ac.gov.br', '20/09/2013', 104, NULL, 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (318, 'Marielle de Oliveira da Silva', 'Encarregada Administrativa', 'mosilva',
        '28276a521c20d0adbfb0ea4afe2c5aee', '882.925.332-49', '700.400-3', '(68)-3226-7799', NULL,
        'marielle.silva@riobranco.ac.gov.br', '20/09/2013', 396, NULL, 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (319, 'Aila Freitas Pires', 'Secretária da PROJURI', 'afpires', '28276a521c20d0adbfb0ea4afe2c5aee',
        '887.674.692-72', '702.090-2', '(68)-3226-7799', NULL, 'afpires@riobranco.ac.gov.br', '21/12/2010', 396, NULL,
        1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (320, 'Ana Caroliny Silva Afonso Cabral', 'Procuradora Jurídica', 'asafonso', '28276a521c20d0adbfb0ea4afe2c5aee',
        '484.425.772-20', '545.541-4', '(68)-3226-7799', NULL, 'ana.cabral@riobranco.ac.gov.br', '20/09/2013', 22, NULL,
        1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (321, 'Emerson de Lucena Mourão', NULL, 'elmourao', 'da8268f65e7a81caaff52b2b04780ca5', '846.653.949-20',
        '700.270-02', '(68)-3211-2226', NULL, 'emerson.mourao@riobranco.ac.gov.br', '20/09/2013', 188, NULL, 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (322, 'Halison Antônio Fernandes de Souza', NULL, 'hasouza', '28276a521c20d0adbfb0ea4afe2c5aee',
        '712.825.302-59', '703.725', '(68)-3211-2226', NULL, 'hasouza@riobranco.ac.gov.br', '24/03/2017', 188, NULL, 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (323, 'Carla de Castro Grutt', 'Chefe da Divisão de Recursos Ambientais', 'ccgrutt',
        '28276a521c20d0adbfb0ea4afe2c5aee', '072.731.527-73', '545461-03', '(68)-3228-3326', NULL,
        'ccgrutt@riobranco.ac.gov.br', '26/01/2011', 98, NULL, 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (324, 'Yonara Maria Cordeiro de Souza', 'Assessora de licitações, contratos e convênios', 'ymsouza',
        '4e20ca29bc3fbd190f385817fe149e24', '036.227.844-07', NULL, '(68)-2106-8025', NULL,
        'yonara.souza@riobranco.ac.gov.br', '20/09/2013', 96, NULL, 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (325, 'Danuza Magalhães de Lemos', 'Chefe de departamento', 'dmlemos', '28276a521c20d0adbfb0ea4afe2c5aee', NULL,
        NULL, NULL, NULL, 'dmlemos@riobranco.ac.gov.br', '15/02/2011', 2, 'magalhaes.danuza@gmail.com', 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (326, 'Débora Maria Pinto Braidi', 'Fiscal de Urbanismo', 'dpbraidi', '28276a521c20d0adbfb0ea4afe2c5aee',
        '322.127.042-91', '9032', '(68)-2106-8001', '(68)-9979-5149', 'dpbraidi@riobranco.ac.gov.br', '25/03/2011', 179,
        NULL, 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (327, 'Wilton Cezar de Jesus Sales de Oliveira', 'Gerente da Fiscalização Urbana', 'wcoliveira',
        '28276a521c20d0adbfb0ea4afe2c5aee', '427.594.042-34', '701582-01', NULL, '(68)-9985-3012',
        'wcoliveira@riobranco.ac.gov.br', '25/03/2011', 179, NULL, 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (328, 'Andrêssa Frota Barbosa', 'Aux. Administrativo', 'afbarbosa', '28276a521c20d0adbfb0ea4afe2c5aee',
        '013.884.772-03', NULL, NULL, '(68)-9209-4841', 'afbarbosa@riobranco.ac.gov.br', '06/04/2011', 179,
        'andressa.f.17@hotmail.com', 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (329, 'Pedro Veras de Almeida Neto', 'técnico de suporte', 'pvneto', 'acbb00bde34f97652a4fc5c052189090',
        '564.938.942-04', NULL, NULL, NULL, 'pvneto@riobranco.ac.gov.br', '10/01/2013', 71, NULL, 0);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (330, 'Elias de Lima', 'Assessor Administrativo e financeiro', 'elima', '28276a521c20d0adbfb0ea4afe2c5aee',
        '475.998.989-72', NULL, '(32)-1142-52', NULL, 'elias.lima@riobranco.ac.gov.br', '20/09/2013', 645, NULL, 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (331, 'Julio César Mapiano', 'Assessor Técnico', 'jcmapiano', '28276a521c20d0adbfb0ea4afe2c5aee',
        '883.376.002-25', NULL, '(68)-3228-4995', NULL, 'julio.mapiano@riobranco.ac.gov.br', '20/09/2013', 72, NULL, 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (332, 'Luciana Meireles Araújo', 'Secretária', 'lmeireles', '28276a521c20d0adbfb0ea4afe2c5aee', '517.335.362-72',
        NULL, '(68)-3210-6802', NULL, 'lu.meireles@bol.com.br', '21/10/2013', 170, NULL, 0);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (333, 'Izabelli Barboza Lopes Ribeiro', 'Téc. Gestão Pública', 'bbribeiro', '28276a521c20d0adbfb0ea4afe2c5aee',
        '852.898.092-87', '702403-1', '(68)-3211-2434', NULL, 'bbribeiro@riobranco.ac.gov.br', '21/11/2017', 344, NULL,
        1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (334, 'Roseane Ramos de Oliveira', 'Merendeira', 'rrsantos', '28276a521c20d0adbfb0ea4afe2c5aee',
        '444.197.912-68', '542957-1', '(68)-3211-2414', NULL, 'roseane.oliveira@riobranco.ac.gov.br', '08/01/2014', 344,
        NULL, 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (335, 'Kelly Christine Fontenele Gouveia', 'comissionado', 'kcfontenele', '28276a521c20d0adbfb0ea4afe2c5aee',
        '300.931.713-15', NULL, '(32)-2823-94', NULL, 'kelly.gouveia@riobranco.ac.gov.br', '20/09/2013', 98, NULL, 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (336, 'Herilândia Sousa da Silva', 'Cargo em Comissão', 'hssilva', '7bbeecdd901b8144572d5e2f5d293855',
        '652.405.612-49', '071089-3', NULL, '(68)-9214-1444', 'hssilva@riobranco.ac.gov.br', '29/08/2012', 294,
        'heribrun@gmail.com', 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (337, 'Marcos Francisco de O. Menezes', 'estagiário', 'mfmenezes', '28276a521c20d0adbfb0ea4afe2c5aee',
        '002.764.782-07', NULL, '(68)-3211-4133', '(68)-9975-4757', 'mfmenezes@riobranco.ac.gov.br', '13/01/2016', 69,
        NULL, 0);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (338, 'Mônica Janaína Meireles', 'TGP', 'mjmeireles', '28276a521c20d0adbfb0ea4afe2c5aee', '889.610.362-20', NULL,
        '(68)-8415-7165', NULL, 'mjmeireles@riobranco.ac.gov.br', '05/01/2016', 28, NULL, 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (339, 'Antônio Nepomuceno de Oliveira', 'técnico', 'aonepomuceno', '28276a521c20d0adbfb0ea4afe2c5aee',
        '002.723.827-80', NULL, '(68)-3211-4133', '(68)-9951-8617', 'aonepomuceno@riobranco.ac.gov.br', '28/01/2013',
        71, 'antoniooliveiraac@gmail.com', 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (340, 'José Ribeiro Junior', 'técnico', 'jdjunior', '28276a521c20d0adbfb0ea4afe2c5aee', '763.382.902-87', NULL,
        NULL, '(92)-3229-00', 'jdjunior@riobranco.ac.gov.br', '28/01/2013', 71, 'jr.ribeiroac@gmail.com', 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (341, 'Luiz Carlos de Assis Filho', 'técnico', 'lcfilho', '28276a521c20d0adbfb0ea4afe2c5aee', '946.563.122-15',
        NULL, NULL, '(99)-3617-96', 'lcfilho@riobranco.ac.gov.br', '28/01/2013', 71, 'luizcarlos_assis@hotmail.com', 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (342, 'Andressa da Silva Melo', 'Assessora Jurídica', 'asmelo', '7b337545e91f114e9f2fec99ea3c3400',
        '946.595.592-20', '7056151', '(68)-3212-7009', NULL, 'atosoficiais@riobranco.ac.gov.br', '20/09/2013', 2,
        'andressa.melo@riobranco.ac.gov.br', 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (343, 'Gerlúcia Afonso de Almeida Magalhães', 'Assessora Jurídica', 'gamagalhaes',
        '28276a521c20d0adbfb0ea4afe2c5aee', '818.400.552-00', '7056361', '(68)-3212-7009', NULL,
        'atosoficiais@riobranco.ac.gov.br', '23/04/2019', 10, 'gerlucia.afonso@riobranco.ac.gov.br', 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (344, 'Maria Aurivan Araújo Prado', 'Assessora Jurídica', 'maprado', '67bf2bf23d38bd62c75d76de943b7f82',
        '662.669.392-15', '7056461', NULL, NULL, 'atosoficiais@riobranco.ac.gov.br', '20/09/2013', 2,
        'maria.prado@riobranco.ac.gov.br', 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (345, 'MARCOS JOSIAS DE SOUZA', 'ASSESSOR', 'mjosias', '5716a5bf0bf0b3028c44e7e34bdf0ab5', NULL, '705682-1',
        NULL, '(68)-9997-9088', 'mjosias@riobranco.ac.gov.br', '31/01/2013', 95, NULL, 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (346, 'Francisca Marisanta Afonso Gama', 'Chefe de Gabinete', 'fmgama', '2b25f0a1334e89059acbd852aac78d4f',
        '510.210.562-00', '7056381', '(68)-3212-7008', NULL, 'atosoficiais@riobranco.ac.gov.br', '20/09/2013', 2,
        'francisca.marisanta@riobranco.ac.gov.br', 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (347, 'Afonso Henrique de Souza Lima', 'Auditor Fiscal', 'ahlima', '28276a521c20d0adbfb0ea4afe2c5aee',
        '138.379.312-34', '2607', NULL, '(68)-9952-9493', 'lima.afonso@gmail.com', '01/04/2013', 179,
        'ahlima@riobranco.ac.gov.br', 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (348, 'Maria Roxiane dos Santos Oliveira', NULL, 'roxiane.oliveira', 'e6a5f4f4f82ecd5ed42a26eb81a5be3a',
        '652.729.942-72', '703730-1', '(68)-3211-2418', NULL, 'roxiane.oliveira@riobranco.ac.gov.br', '04/09/2014', 344,
        NULL, 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (349, 'Milena de Souza Nascimento', NULL, 'msnascimento', '1449a86bb7548b6c6cb561562715bf68', '623.464.742-15',
        '543462-1', '(68)-3211-2418', NULL, 'milena.souza@riobranco.ac.gov.br', '20/09/2013', 344, NULL, 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (350, 'Saimon Perceu Malaquias Leite', 'Assessor Jurídico', 'saimonperceu.leite',
        '28276a521c20d0adbfb0ea4afe2c5aee', '920.692.662-49', '706174', '(68)-3212-7009', NULL,
        'saimon.leite@riobranco.ac.gov.br', '20/09/2013', 2, 'saimonsperceu@gmail.com', 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (351, 'Maylon Taumaturgo Oliveira', 'Assessor Jurídico', 'maylon.oliveira', '28276a521c20d0adbfb0ea4afe2c5aee',
        '936.228.212-72', '706144', '(68)-3212-7009', NULL, 'maylon.oliveira@riobranco.ac.gov.br', '15/04/2013', 2,
        NULL, 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (352, 'Hélia de Sousa Ribeiro', 'Auxiliar administrativo', 'hribeiro', '28276a521c20d0adbfb0ea4afe2c5aee',
        '527.264.952-49', NULL, '(68)-3222-7229', NULL, 'hribeiro@riobranco.ac.gov.br', '22/04/2013', 179,
        'heliaribeiro182@hotmail.com', 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (353, 'Vanussa Vanessa Moraes', 'Técnica em Gestão Pública', 'vvmoraes', '8297d4d6e30bdbdbf6046914078d5c87',
        '624.078.482-68', '70441801', '(68)-3222-7229', NULL, 'vanussa.moraes@riobranco.ac.gov.br', '21/10/2013', 179,
        'wanessaapx78@gmail.com', 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (354, 'Mara Elfrida da Silva Rodrigues', 'Gerente administrativa e financeira', 'mara.rodrigues',
        '28276a521c20d0adbfb0ea4afe2c5aee', '516.398.072-68', NULL, '(68)-3211-2203', NULL,
        'mara.rodrigues@riobranco.ac.gov.br', '03/05/2013', 935, NULL, 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (355, 'Elizângela Amorim Lima', NULL, 'elizangela.lima', '28276a521c20d0adbfb0ea4afe2c5aee', '434.324.762-72',
        NULL, '(68)-3211-2418', NULL, 'elizangela.lima@riobranco.ac.gov.br', '20/09/2013', 344, NULL, 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (356, 'Osmar Costa dos Santos', 'Técnico', 'osmar.santos', '28276a521c20d0adbfb0ea4afe2c5aee', '683.069.622-49',
        '702782-1', NULL, NULL, 'osmar.santos@riobranco.ac.gov.br', '20/09/2013', 344, NULL, 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (357, 'José Lopes Sobrinho Netto', 'Estagiário', 'jose.sobrinho', '73e0f7487b8e5297182c5a711d20bf26',
        '017.521.132-92', NULL, NULL, NULL, 'jose.sobrinho@riobranco.ac.gov.br', '02/10/2013', 170,
        'lopes.ka@hotmail.com', 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (358, 'Shirley Daiane dos Anjos Silva', NULL, 'shirley.silva', '81ac18729fdac1ee619cabf920c95d69',
        '003.743.232-08', NULL, '(68)-3223-6007', NULL, 'shirley.silva@riobranco.ac.gov.br', '10/03/2014', 645, NULL,
        1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (359, 'Rozamélia Souza de Misquita', NULL, 'rozamelia.misquita', '93aa668f93ee20ade7cf5f60a60a4545',
        '668.914.422-49', '272983', '(68)-3223-6007', NULL, 'rosamelia.unv@hotmail.com', '14/11/2013', 645,
        'rozamelia.misquita@riobranco.ac.gov.br', 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (360, 'Alfredo Renato Pena Brana', 'Chefe do Cadastro Imobiliário', 'alfredo.brana',
        '28276a521c20d0adbfb0ea4afe2c5aee', '133.381.292-20', '1996', '(68)-3223-6474', NULL,
        'alfredo.brana@riobranco.ac.gov.br', '25/11/2013', 645, NULL, 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (361, 'Ana Paula Maia Estevão', 'Surpervisora', 'ana.estevao', '28276a521c20d0adbfb0ea4afe2c5aee',
        '005.124.032-79', NULL, '(68)-9236-0025', NULL, 'paulamaiaeste200@gmail.com', '25/11/2013', 645,
        'ana.estevao@riobranco.ac.gov.br', 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (362, 'Pietra de Oliveira Araújo', NULL, 'pietra.araujo', 'a6fbb5817ba22454cb640cec36f3ee16', '002.166.972-47',
        NULL, '(68)-3223-6007', NULL, 'pietra.araujo@riobranco.ac.gov.br', '11/03/2014', 645,
        'pietra.araujo@hotmail.com', 0);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (363, 'Nilcilene Braga de Lima Veiber', NULL, 'nilcilene.veiber', '28276a521c20d0adbfb0ea4afe2c5aee',
        '216.560.962-34', '13846', NULL, NULL, 'nilcilene.veiber@riobranco.ac.gov.br', '26/02/2014', 179, NULL, 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (364, 'Adercio Belmont de Lima', NULL, 'adercio.lima', 'aa6a9a018e168a634750dce94d729393', '040.759.602-00',
        '700054', '(68)-3223-1214', NULL, 'adercio.lima@riobranco.ac.gov.br', '03/04/2014', 368, NULL, 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (365, 'SAMUEL ANTONIO RIBEIRO VIEIRA ALVES', 'Estagiário', 'samuel.alves', '28276a521c20d0adbfb0ea4afe2c5aee',
        NULL, NULL, NULL, NULL, 'samuel.alves@riobranco.ac.gov.br', '11/09/2014', 4, NULL, 0);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (366, 'Paulo Weverton Gomes de Almeida', 'Estagiário', 'paulo.almeida', 'a45958517604f5cd90d6ee51ad9cfdb6', NULL,
        NULL, NULL, NULL, 'paulo.almeida@riobranco.ac.gov.br', '11/09/2014', 4, NULL, 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (367, 'Suzane Maria Ferreira da Silva', 'Analista de Sistemas', 'suzane.ferreira',
        '5675ffcb0b318e9c7663c627782d6106', '736.628.602-87', NULL, NULL, NULL, 'suzane.ferreira@riobranco.ac.gov.br',
        '10/01/2015', 4, 'suzinha.fer@gmail.com', 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (368, 'ICARO FERNANDES RAMOS', 'suporte', 'icaro.ramos', '28276a521c20d0adbfb0ea4afe2c5aee', '779.664.812-04',
        NULL, NULL, NULL, 'icaro.ramos@riobranco.ac.gov.br', '10/01/2015', 4, 'icaro_fernandess20@hotmail.com', 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (369, 'MGA', 'Fornecedor', 'mga', '28276a521c20d0adbfb0ea4afe2c5aee', NULL, NULL, NULL, NULL,
        'mga@riobranco.ac.gov.br', '15/01/2015', 4, NULL, 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (370, 'Sulamita da Silva Lima Guedes', NULL, 'sulamita.guedes', '28276a521c20d0adbfb0ea4afe2c5aee',
        '670.981.262-68', NULL, NULL, '9953-1811', 'sulamita.guedes@riobranco.ac.gov.br', '20/02/2015', 95,
        'sulaenf@gmail.com', 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (371, 'Ulderico Queiroz Júnior', 'Chefe do setor', 'ulderico.queiroz', '28276a521c20d0adbfb0ea4afe2c5aee',
        '046.402.108-17', NULL, NULL, NULL, 'ulderico.queiroz@riobranco.ac.gov.br', '05/03/2015', 109, NULL, 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (372, 'Aline Pupio Reis Rodrigues', NULL, 'aline.rodrigues', '28276a521c20d0adbfb0ea4afe2c5aee',
        '619.448.232-04', NULL, NULL, NULL, 'alinepupio25@hotmail.com', '14/03/2019', 100,
        'aline.rodrigues@riobranco.ac.gov.br', 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (373, 'Maria do Socorrro Silva Pompermaier', NULL, 'maria.pompermaier', 'f6f4deb7dad1c2e5e0b4d6569dc3c1c5',
        '597.184.122-04', NULL, NULL, NULL, 'maria.pompermaier@riobranco.ac.gov.br', '08/04/2015', 645, NULL, 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (374, 'Eder Soares Cordeiro', NULL, 'eder.cordeiro', 'f4da79660c30a428f66226f911957571', '002.806.912-97',
        '702385-01', NULL, NULL, 'eder.sc@hotmail.com', '03/10/2017', 645, 'eder.cordeiro@riobranco.ac.gov.br', 0);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (375, 'NEIVA NARA RODRIGUES DA COSTA', NULL, 'neiva.costa', '649361897a222f119a0183e00f9ae1e6', '443.859.782-04',
        NULL, '(99)-4515-82', NULL, 'neiva.costa@riobranco.ac.gov.br', '05/01/2016', 645,
        'neiva.costa@riobranco.ac.gov.br', 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (376, 'WEVERTON DAVILA DE FARIAS', NULL, 'weverton.farias', '8ac769282f194a48546e828c76ac4267', '787.289.831-49',
        NULL, '(68)-3222-8680', NULL, 'weverton.farias@riobranco.ac.gov.br', '05/01/2016', 645,
        'weverton.farias@riobranco.ac.gov.br', 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (377, 'Thiago Franco de Lima', 'TGP', 'thiago.franco', '28276a521c20d0adbfb0ea4afe2c5aee', '856.705.292-00',
        NULL, NULL, NULL, 'thiago.franco@riobranco.ac.gov.br', '06/06/2018', 4, NULL, 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (378, 'Marcos Francisco de Oliveira Menezes', 'Terceirizado', 'marcos.menezes',
        'c83a64227c954c9a5e526c159f269a3a', '002.764.782-07', NULL, NULL, NULL, 'marcos.menezes@riobranco.ac.gov.br',
        '14/01/2016', 4, NULL, 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (379, 'Roberta de Melo Picanço', NULL, 'roberta.picanco', '28276a521c20d0adbfb0ea4afe2c5aee', '643.844.622-04',
        NULL, '(68) 3211-2430', '(68)- 993-9503', 'roberta26mp@bol.com.br', '04/05/2016', 59, NULL, 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (380, 'Rachel Aparecida da S. Costa', NULL, 'rachel.costa', '28276a521c20d0adbfb0ea4afe2c5aee', '924.662.732-68',
        NULL, '((6)-8321-1220', NULL, 'rachel.costa@riobranco.ac.gov.br', '15/03/2017', 288, NULL, 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (381, 'Cleyverton da Silva Lima', NULL, 'cleyverton.lima', '28276a521c20d0adbfb0ea4afe2c5aee', '531.859.602-97',
        NULL, NULL, NULL, 'cleyverton.lima@riobranco.ac.gov.br', '10/02/2017', 4, NULL, 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (382, 'TAINARA CRISTINA DA SILVEIRA', NULL, 'tainara.silveira', '9bf941a64834036354794f206ac4d3ff',
        '029.860.222-94', NULL, NULL, NULL, 'tainara.silveira@riobranco.ac.gov.br', '16/02/2017', 170, NULL, 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (383, 'MILENE ROSA DE ALMEIDA DIAS', NULL, 'milene.dias', '28276a521c20d0adbfb0ea4afe2c5aee', '217.642.858-75',
        NULL, NULL, NULL, 'milene.dias@riobranco.ac.gov.br', '10/02/2017', 170, NULL, 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (384, 'João Paulo Ribeiro Baptista', NULL, 'joao.baptista', '28276a521c20d0adbfb0ea4afe2c5aee', '893.443.592-53',
        NULL, '(68) 2106-8025', NULL, 'joao.baptista@riobranco.ac.gov.br', '11/04/2017', 96, NULL, 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (385, 'Tainã Lima da Costa', NULL, 'taina.lima', '28276a521c20d0adbfb0ea4afe2c5aee', '973.097.702-00', NULL,
        '(68)99978-9163', NULL, 'taina.lima@riobranco.ac.gov.br', '11/04/2017', 96, NULL, 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (386, 'Mayka Mesquita de Matos Oliveira', 'Gerente de Sistemas', 'mayka.mesquita',
        '28276a521c20d0adbfb0ea4afe2c5aee', '523.711.232-34', NULL, NULL, NULL, 'mayka.mesquita@riobranco.ac.gov.br',
        '12/07/2017', 4, NULL, 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (387, 'Erivelton Laia Vieira', NULL, 'erivelton.vieira', '28276a521c20d0adbfb0ea4afe2c5aee', '870.581.622-04',
        NULL, '68999143865', NULL, 'erivelton.laia@gmail.com', '21/07/2017', 935,
        'erivelton.vieira@riobranco.ac.gov.br', 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (388, 'Moisés Gabriel de Morais Filho', NULL, 'moises.filho', '28276a521c20d0adbfb0ea4afe2c5aee',
        '765.070.502.10', NULL, '992118955', NULL, 'moisesmorais49@gmail.com', '21/07/2017', 935,
        'moises.filho@riobranco.ac.gov.br', 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (389, 'Daniele Lima de Souza', NULL, 'daniele.souza', '28276a521c20d0adbfb0ea4afe2c5aee', '946.596.642-87', NULL,
        '(68)-3211-2418', NULL, 'daniele.souza@riobranco.ac.gov.br', '21/11/2017', 344, NULL, 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (390, 'Michelson Frota Barbosa', NULL, 'michelson.barbosa', '28276a521c20d0adbfb0ea4afe2c5aee', '900.602.292-68',
        NULL, NULL, NULL, 'michelson.barbosa@riobranco.ac.gov.br', '21/03/2018', 645, NULL, 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (391, 'Karina Guimarães de Alencar', NULL, 'karina.alencar', '28276a521c20d0adbfb0ea4afe2c5aee',
        '980.417.542-87', NULL, '(68)-3212-7133', '(68)-9994-3714', 'karina.alencar@riobranco.ac.gov.br', '30/04/2018',
        2, 'karina.guimaraesalencar@hotmail.com', 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (392, 'Wilson José das Chagas Sena Leite', NULL, 'wilson.leite', '28276a521c20d0adbfb0ea4afe2c5aee',
        '435.070.182-68', NULL, '(68)-3212-7083', NULL, 'wilson.leite@riobranco.ac.gov.br', '11/06/2018', 99, NULL, 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (393, 'Josué Alexandre de Oliveira Junior', NULL, 'josue.junior', 'ffc925d598567dd06645b5d73b15d255',
        '277.675.628-32', NULL, '(68)-3212-7083', NULL, 'josue.junior@riobranco.ac.gov.br', '11/06/2018', 99, NULL, 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (394, 'Alessandra Pereira de Souza', NULL, 'alessandra.pereira', '28276a521c20d0adbfb0ea4afe2c5aee',
        '763.789.262-04', NULL, NULL, NULL, 'alessandra.pereira@riobranco.ac.gov.br', '28/06/2018', 88, NULL, 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (395, 'Cristiane Greyce Souza Cavalcante Leão', NULL, 'cristiane.leao', '28276a521c20d0adbfb0ea4afe2c5aee',
        '614.695.212-53', NULL, '(68)-9998-2274', NULL, 'cristiane.leao@riobranco.com.br', '13/03/2019', 25, NULL, 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (396, 'Peter Nascimento de Aquino Junior', NULL, 'peter.aquino', '28276a521c20d0adbfb0ea4afe2c5aee',
        '643.821.332-20', NULL, NULL, NULL, 'peter.aquino@riobranco.ac.gov.br', '14/03/2019', 4, NULL, 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (397, 'Samuel Eder Caovilla', NULL, 'samuel.caovilla', 'bb72a58e95d5f6f2afa55716df19114a', '027.626.569-66',
        NULL, NULL, NULL, 'samuel.caovilla@riobranco.ac.gov.br', '27/03/2019', 645, NULL, 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (398, 'Maurilho da Costa Silva', NULL, 'maurilho.silva', '28276a521c20d0adbfb0ea4afe2c5aee', '360.096.282-53',
        NULL, NULL, NULL, 'maurilho.silva@riobranco.ac.gov.br', '08/05/2019', 1028, NULL, 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (399, 'Iracema Moreno Rodrigues Paulo', NULL, 'iracema.rodrigues', '5b81c32faa440d26ac4308e72b600f19',
        '936.456.192-91', NULL, NULL, NULL, 'iracema.rodrigues@riobranco.ac.gov.br', '14/05/2019', 98, NULL, 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (400, 'Yndaiara da Cunha Gomes', NULL, 'yndaiara.gomes', '28276a521c20d0adbfb0ea4afe2c5aee', '013.624.172-70',
        NULL, NULL, NULL, 'yndaiara.gomes@riobranco.ac.gov.br', '03/06/2019', 98, NULL, 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (401, 'Jothania do Carmo Silva', NULL, 'jothania.silva', 'ef6a940c261f732fff31cbb27ca6964a', '996.810.642-91',
        NULL, NULL, NULL, 'jothania.silva@riobranco.ac.gov.br', '04/10/2019', 645, NULL, 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (402, 'Iza Bruna Vieira de Souza', NULL, 'iza.souza', '28276a521c20d0adbfb0ea4afe2c5aee', '891.781.302-04', NULL,
        NULL, NULL, 'iza.souza@riobranco.ac.gov.br', '27/11/2019', 95, NULL, 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (403, 'Saluana Bonfim do Nascimento', NULL, 'saluana.nascimento', '28276a521c20d0adbfb0ea4afe2c5aee',
        '573.852.892-15', NULL, NULL, NULL, 'saluana.nascimento@riobranco.ac.gov.br', '02/12/2019', 95, NULL, 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (404, 'Luiz Felipe Magalhães da Silva', NULL, 'luiz.silva', '28276a521c20d0adbfb0ea4afe2c5aee', '014.211.522-31',
        NULL, NULL, NULL, 'luiz.silva@riobranco.ac.gov.br', '05/12/2019', 95, NULL, 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (405, 'Ana Cláudia dos Santos Souza', NULL, 'ana.claudia', '28276a521c20d0adbfb0ea4afe2c5aee', '020.605.082-82',
        NULL, NULL, NULL, 'ana.claudia@riobranco.ac.gov.br', '16/12/2019', 95, NULL, 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (406, 'João Felipe Barauna de Souza', NULL, 'joaofelipe.souza', '28276a521c20d0adbfb0ea4afe2c5aee',
        '877.347.432-04', NULL, NULL, NULL, 'joaofelipe.souza@riobranco.ac.gov.br', '16/12/2019', 95, NULL, 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (407, 'Luiz Webister Marinho Aguirre', NULL, 'luiz.aguirre', '6c47f1ca7ab83ffa441d1d65b0561b2d', NULL, NULL,
        NULL, NULL, 'luiz.aguirre@riobranco.ac.gov.br', '27/04/2021', 99, NULL, 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (408, 'Catharine Neves Fernandes', NULL, 'catharine.neves', 'bffc764900a207e700f9cac39a97fa28', NULL, NULL, NULL,
        NULL, 'catharine.neves@riobranco.ac.gov.br', '27/04/2021', 99, NULL, 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (409, 'Wilson Madeira Carvalho', NULL, 'wilson.carvalho', '67b4c19380d1d81c938dbfff72f7ee7e', NULL, NULL, NULL,
        NULL, 'wilson.carvalho@riobranco.ac.gov.br', '27/04/2021', 99, NULL, 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (410, 'Rogério Gonçalves Bezerra', NULL, 'rogerio.bezerra', 'bd28aa3ff67a2d813fc75a5b6f9144cc', NULL, NULL, NULL,
        NULL, 'rogerio.bezerra@riobranco.ac.gov.br', '27/04/2021', 99, NULL, 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (411, 'Márcio James Caruta Geber', NULL, 'marcio.geber', '59077bd127572894262fde47bf905806', NULL, NULL, NULL,
        NULL, 'marcio.geber@riobranco.ac.gov.br', '27/04/2021', 99, NULL, 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (412, 'Jaime Afonso Viana Fontes', NULL, 'jaime.fontes', 'df2fa10c3af4d7d7359aa0ca1de95861', NULL, NULL, NULL,
        NULL, 'jaime.fontes@riobranco.ac.gov.br', '0000-00-00', 0, NULL, 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (413, 'Eduardo Alves da Silva', NULL, 'eduardo.silva', '4578c1c50f17f2647ae11848f7e302f6', NULL, NULL, NULL,
        NULL, 'eduardo.silva@riobranco.ac.gov.br', '15/07/2021', 0, NULL, 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (414, 'Geraldo Pereira Maia Filho', NULL, 'geraldo.filho', '685e3e904775db963c877be16888c7c9', NULL, NULL, NULL,
        NULL, 'geraldo.filho@riobranco.ac.gov.br', '15/07/2021', 0, NULL, 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (415, NULL, NULL, NULL, '28276a521c20d0adbfb0ea4afe2c5aee', NULL, NULL, NULL, NULL, NULL, '23/11/2021', 0, NULL,
        NULL);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (416, 'Francisco Vanderlan Nogueira', NULL, 'vanderlan.nogueira', '40eccd29373a79b2457095cf16cdd147',
        '21639400206', 'MAT-2020', NULL, NULL, 'vanderlan.nogueira@riobranco.ac.gov.br', '08/12/2021', 99, NULL, 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (417, 'Geverson Menezes Muniz', NULL, 'geverson.muniz', '71bbfaaee9f973c7ad5c37e645103608', '46482040772', NULL,
        NULL, NULL, 'geverson.muniz@riobranco.ac.gov.br', '08/12/2021', 99, NULL, 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (418, 'Claudia Maria Pinho Silva', NULL, 'claudia.pinho', '9e4f5a71b6efc77e2a8cbe0277623862', '19668945204',
        'MAT-58785', NULL, NULL, 'claudia.pinho@riobranco.ac.gov.br', '08/12/2021', 99, NULL, 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (419, 'Marinho Mendes Brasil', NULL, 'marinho.mendes', 'df9c4fa62098bc4bf65cfccebdb371e5', '13823566253',
        'MAT-4189', NULL, NULL, 'marinho.mendes@riobranco.ac.gov.br', '08/12/2021', 99, NULL, 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (420, 'Paulo Teixeira Bezerra Neto', NULL, 'paulo.bezerra', 'dc79dfc484e5cd7e250ed6c36cb79f21', '6576699204',
        'MAT-4103', NULL, NULL, 'paulo.bezerra@riobranco.ac.gov.br', '08/12/2021', 0, NULL, 1);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (421, NULL, NULL, NULL, '28276a521c20d0adbfb0ea4afe2c5aee', NULL, NULL, NULL, NULL, NULL, '06/02/2022', 0, NULL,
        NULL);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (422, NULL, NULL, NULL, '28276a521c20d0adbfb0ea4afe2c5aee', NULL, NULL, NULL, NULL, NULL, '09/02/2022', 0, NULL,
        NULL);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (423, NULL, NULL, NULL, '28276a521c20d0adbfb0ea4afe2c5aee', NULL, NULL, NULL, NULL, NULL, '12/02/2022', 0, NULL,
        NULL);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (424, NULL, NULL, NULL, '28276a521c20d0adbfb0ea4afe2c5aee', NULL, NULL, NULL, NULL, NULL, '22/04/2022', 0, NULL,
        NULL);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (425, NULL, NULL, NULL, '28276a521c20d0adbfb0ea4afe2c5aee', NULL, NULL, NULL, NULL, NULL, '09/06/2022', 0, NULL,
        NULL);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (426, 'Eder Soares Cordeiro', 'Agente Administrativo', 'escordeiro', 'f7e5dcffd126438bb873b617b6555932',
        '002.806.912-97', '702385-1', NULL, '68 9 99841444', 'eder.cordeiro@riobranco.ac.gov.br', '0000-00-00', 646,
        NULL, NULL);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (427, NULL, NULL, NULL, '28276a521c20d0adbfb0ea4afe2c5aee', NULL, NULL, NULL, NULL, NULL, '29/07/2022', 0, NULL,
        NULL);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (428, NULL, NULL, NULL, '28276a521c20d0adbfb0ea4afe2c5aee', NULL, NULL, NULL, NULL, NULL, '15/09/2022', 0, NULL,
        NULL);
INSERT INTO USUARIOALVARASEKER(IdUsuario, NomeUsuario, CargoUsuario, LoginUsuario, SenhaUsuario, CpfUsuario, MatUsuario,
                               TelUsuario, CelUsuario, EmailUsuario, DataCadastro, IdUniOrg, EmailAlternativo, FIELD14)
VALUES (429, NULL, NULL, NULL, '28276a521c20d0adbfb0ea4afe2c5aee', NULL, NULL, NULL, NULL, NULL, '17/09/2022', 0, NULL,
        NULL);
