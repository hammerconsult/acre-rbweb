CREATE TABLE responsaveltecnicoseker
(
    IdResponsavelTecnico NUMERIC(19, 0) NOT NULL PRIMARY KEY,
    Nome                 VARCHAR(255)   NOT NULL,
    CREA                 VARCHAR(255)   NOT NULL,
    Especializacao       VARCHAR(255),
    UF                   VARCHAR(255)   NOT NULL,
    FK_especializacaoID  INTEGER        NOT NULL,
    Orgao                INTEGER        NOT NULL,
    FIELD8               VARCHAR(255),
    FIELD9               VARCHAR(255)
);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (1, 'Jorge Abrahão Judar Júnior', 'A122432', 'Arquiteto', '0', 3, 2, NULL, '1 = CREA');
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (2, 'João Furtado de Lima', '102054738', 'Técnologo', '0', 4, 1, NULL, '2 = CAU');
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (3, 'Thiago Lima de Souza', '100437524', 'Engenheiro Civil', '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (4, 'Carlos Alberto Sgrinholi', 'A177938', 'Arquiteto', '0', 3, 2, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (5, 'Jairo Castro da Penha', '1507004362', 'Engenheiro Civil', '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (6, 'Francisco de Assis Dantas Júnior', '107189461', 'Engenheiro Civil', '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (7, 'Sebastião Aguiar da Fonseca Dias', '101974914', 'Engenheiro Civil', '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (8, 'Rafael Wiciuk', '10071960', 'Engenheiro Civil', '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (9, 'Giulliano Ribeiro da Silva', '100737455', 'Engenheiro Civil', '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (10, 'Eneias Marques Júnior', '1305470117', 'Engenheiro Civil', '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (11, 'Mário Cezar de Macedo', '2326', 'Técnologo', 'AC', 4, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (12, 'Abdel Barbosa Derze', '2600', 'Engenheiro Civil', 'AC', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (13, 'João Nishihira', '407045872', 'Engenheiro Civil', '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (14, 'Ana Maria Cardoso Cunha Araújo', 'A206091', 'Arquiteta e Urbanista', '0', 2, 2, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (15, 'Edilene de Freitas Silva Cosson Mota', '4941', 'Engenheira Civil', 'AC', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (16, 'Francisco Nilo Barreto Júnior', '104227125', 'Engenheiro Civil', '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (17, 'Willian Abreu da Silva', 'A710199', 'Arquiteto e Urbanista', '0', 2, 2, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (18, 'Charbel Boutros Kassab', 'A27580-8', 'Arquiteto e Urbanista', '0', 2, 2, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (19, 'Laís Veloso Ribeiro Buzolin', 'A691976', 'Arquiteta e Urbanista', 'AC', 2, 2, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (20, 'Erivaldo Rodrigues do Nascimento Oliveira', '107188139', 'Engenheiro Civil', '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (21, 'N'' Diogou Diene', '7928', 'Arquiteto e Urbanista', 'DF', 2, 2, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (22, 'João Alberto Lisboa Assumpção', '4800', 'Engenheiro Civil', 'MT', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (23, 'João Paulo Lima Bessa', 'A431524', 'Arquiteto e Urbanista', '0', 2, 2, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (24, 'Valmir Alexandre Medici', '2606121198', 'Engenheiro Civil', '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (25, 'Marcelo de Vasconcelos Borges', '8379', 'Engenheiro Civil', 'AC', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (26, 'Luis Guilherme G. O. Bacchi', 'A253049', 'Arquiteto e Urbanista', '0', 2, 2, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (27, 'Jurandyr Rodrigues da Silva Junior', '106134817', 'Engenheiro Civil', '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (28, 'Anderson Martins Nascimento', '101451873', 'Engenheiro Civil', '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (29, 'Antonio Ramos de Moura', '106468936', 'Técnologo', 'AC', 4, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (30, 'Gustavo Menezes Mateus', '2609351496', 'Engenheiro Civil', '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (31, 'Edivaldo Rodrigues da Silva', '2684', 'Técnologo', 'AC', 4, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (32, 'Pedro Sezo Borges', '106135236', 'Engenheiro Civil', '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (33, 'Eduarda Montenegro Gonçalves Pinheiro', '613655222', 'Engenheiro Civil', '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (34, 'Raul Marcel Ferreira da Silveira', '8622', 'Engenheiro Civil', 'AC', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (35, 'Joaquim Ferreira do Nascimento Júnior', '68242', 'Engenheiro Civil', 'MG', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (36, 'Raimundo Barroso Carvalho', '100092403', 'Engenheiro Civil', '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (37, 'David Barrozo Alves de Sousa', '7924585249', 'Técnologo', '0', 4, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (38, 'Laís Medeiros de Araújo', 'A592226', 'Arquiteto e Urbanista', '0', 2, 2, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (39, 'José Ribeiro de Loiola Neto', 'A756458', 'Arquiteto', '0', 2, 2, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (40, 'Diana Maria Rogério do Vale', '113840179', 'Técnologo', '0', 4, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (41, 'Jean Carlos Moreira de Mesquita', '9660', 'Engenheiro Civil', 'AC', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (42, 'César Henrique Oliveira de Menezes', 'A548910', 'Arquiteto e Urbanista', '0', 2, 2, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (43, 'Rodolfo Quiroga Elias', 'A30326-7', 'Arquiteto e Urbanista', '0', 2, 2, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (44, 'Daniel Francisco da Silva', 'A1663763', 'Técnologo', '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (45, 'Fábio Crisóstomo Jucá', '8592', 'Engenheiro Civil', 'AC', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (46, 'Vinicius Saddock da Rocha', '2308960698', 'Engenheiro Civil', '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (47, 'Soraya Saraiva Andrade', 'A692034', 'Arquiteta e Urbanista', '0', 2, 2, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (48, 'Rafael Costa de Albuquerque', 'A546984', 'Arquiteto e Urbanista', '0', 2, 2, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (49, 'Mauro Patrick Cardoso Modesto', 'A76415-9', 'Arquiteto e Urbanista', '0', 2, 2, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (50, 'Iara Barbosa de Sousa', '106236601', 'Engenheira Civil', 'AC', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (51, 'Benilson de Oliveira Rocha', '100766552', 'Técnologo', '0', 4, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (52, 'Marivaldo Almeida de Oliveira', '7082', 'Engenheiro Civil', 'AC', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (53, 'Sâmella Araujo Lopes de Melo', '5063336208', 'Arquiteta e Urbanista', 'SP', 2, 2, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (54, 'Hebert Mariano Costa de Oliveira', 'A317250', 'Arquiteto e Urbanista', '0', 2, 2, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (55, 'Laessa Cristina Estrela Silva', '9332', 'Arquiteta e Urbanista', 'GO', 2, 2, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (56, 'Boris Madsen Cunha', '15279', 'Arquiteto e Urbanista', 'PR', 2, 2, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (57, 'Paulo Roberto Negreiros Negrini', '5060675460', 'Arquiteto e Urbanista', 'SP', 2, 2, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (58, 'Valdeci Candido de Lima Ribeiro', 'A111066', 'Arquiteta', '0', 3, 2, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (59, 'Clênio Plauto de Souza Farias', 'A253073', 'Arquiteto e Urbanista', '0', 2, 2, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (60, 'Jamyl Asfury Martins Oliveira', '8030', 'Engenheiro Civil', 'AC', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (61, 'João Vilarinho Amaral', '101442483', 'Engenheiro Civil', '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (62, 'Roney Alves das Neves', 'A13080', 'Arquiteto', '0', 3, 2, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (63, 'Marlene Rodrigues Gonçalves Caetano', 'A107786', 'Arquiteta e Urbanista', '0', 2, 2, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (64, 'José Cristiano Lima de Matos', '6846', 'Tecnólogo', 'AC', 4, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (65, 'Charlei Jorge  de Oliveira Albuquerque', '106243012', 'Tecnólogo', '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (66, 'Antonio Pantoja Vieira Neto', '1514745429', 'Engenheiro Civil', '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (67, 'Gabriel Assumpção Firmo Dantas', '9637', 'Engenheiro Civil', 'AC', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (68, 'Aluildo de Moura Oliveira', 'A71017-2', 'Tecnólogo', '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (69, 'João Pereira de Lima', '102636699', 'Engenheiro Civil', '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (70, 'Sanderson Sales de Oliveira', '100195598', 'Tecnólogo', '0', 4, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (71, 'Paulo Franco Teles de Oliveira', 'A694096', 'Arquiteto e Urbanista', '0', 2, 2, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (72, 'Manoel Graciano da Costa', '104406933', 'Tecnólogo', '0', 4, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (73, 'João Roberto de Araújo Campos', '9240', 'Engenheiro Civil', 'AC', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (74, 'Rodrigo Ribeiro de Moura Leite', '5062024073', 'Engenheiro Civil', '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (75, 'Alexandre Cabral Cavalcanti', '881002322', 'Engenheiro Civil', 'RJ', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (76, 'Luiz Carlos Saito', '1463', 'Engenheiro Civil', 'AC', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (77, 'Petronio Aparecido Chaves Antunes', '1701614928', 'Engenheiro Civil', '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (78, 'Chárdyan Goncalves Sahid', '8033', 'Engenheiro Civil', 'AC', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (79, 'Alexandre Vieira Silva', '2612521682', 'Engenheiro Civil', '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (80, 'Antonio Carlos Gomes', '6228', 'Engenheiro Civil', 'CE', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (81, 'Marcela Janara Ardaia de Oliveira', '8987', 'Engenheira Civil', 'AC', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (82, 'Luiz Antonio da Silveira Caetano', '101005105', 'Engenheiro Civil', '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (83, 'Fábio Augusto Araújo Gallo', '8314', 'Engenheiro Civil', 'AC', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (84, 'João Pereira de Lima', '7954', 'Engenheiro Civil', 'AC', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (85, 'Lidianna Sousa de Almeida Sasai', '100994032', 'Engenheira Civil', '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (86, 'Edinete Oliveira', 'A18167-6', 'Arquiteta e Urbanista', '0', 2, 2, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (87, 'Francisco Airton Davila Lucena', '106201670', 'Engenheiro Civil', '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (88, 'Cledson Jardim de Araújo', '9632', 'Engenheiro Civil', 'AC', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (89, 'Narciso Mendes de Assis Júnior', '704575760', 'Engenheiro Civil', '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (90, 'Leandro Coradin', '79.163', 'Arquiteto e Urbanista', 'PR', 2, 2, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (91, 'Flávio Soares Santos', 'A21250-4', 'Arquiteto e Urbanista', '0', 2, 2, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (92, 'Geraldo de Souza Ribeiro Filho', '521712', 'Engenheiro Civil', 'SP', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (93, 'Haroldo Pinheiro Villar de Queiroz', '3938', 'Arquiteto e Urbanista', 'DF', 2, 2, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (94, 'Antônio Salomão Lamar Neto', 'A21490-6', 'Arquiteto e Urbanista', '0', 2, 2, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (95, 'Weisser Coelho da Silva', '10922140', 'Engenheiro Civil', '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (96, 'Emerson Deângelis Simplicio dos Santos', 'A52579-0', 'Arquiteto e Urbanista', '0', 2, 2, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (97, 'Reginey de Souza', '105077704', 'Engenheiro Civil', '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (98, 'José Brasileiro Borges', '101911750', 'Engenheiro Civil', '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (99, 'Thiego Lima de Souza', '106234587', 'Engenheiro Civil', '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (100, 'Lander Lucas Barbosa', '60.132-2', 'Arquiteto e Urbanista', '0', 2, 2, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (101, 'João Vicente Azambuja', '742', 'Engenheiro Civil', 'MS', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (102, 'Irineu Aparecido Batista da Cunha', '4587', 'Engenheiro Civil', 'AC', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (103, 'Luciano Freire de Carvalho', 'A69195-0', 'Arquiteto e Urbanista', '0', 2, 2, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (104, 'Luzimar de Souza Santos', '6980', 'Técnologo', 'AC', 4, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (105, 'José Benildo Oliveira Rocha', '12231', 'Engenheiro Civil', 'PE', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (106, 'Erick Anderson da Silva Mendonça', 'A48526-8', 'Arquiteto e Urbanista', '0', 2, 2, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (107, 'Anderson Amaro Lopes de Almeida', 'A69193-3', 'Engenheiro Civil', 'AC', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (108, 'Mário Jorge dos Santos Ferreira', '6121', 'Técnologo', 'AC', 4, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (109, 'José Mauricio Escobari Jimenez', '108447472', 'Engenheiro Civil', '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (110, 'Fabiana Barroso de Souza', '100471897', 'Arquiteta e Urbanista', '0', 1, 2, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (111, 'José Eugênio Castilho', '105875171', 'Engenheiro Civil', '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (112, 'Vislan Campos dos Reis', '7995', 'Engenheiro Civil', 'AC', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (113, 'Angélica Albuquerque da Silva Macêdo', '101160631', 'Engenheira Civil', '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (114, 'Ione Maria Jalul Araújo Alexandria', '338605', 'Arquiteta e Urbanista', '0', 2, 2, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (115, 'Andrea Soares Goes', '39304', 'Engenheiro Civil', 'BA', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (116, 'Guilherme David Marques Alexandre', '105503606', 'Engenheiro Civil', '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (117, 'Glayton Pinheiro Rego', '103833200', 'Engenheiro Civil', '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (118, 'Jó Paulino de Freitas', '6866', 'Técnologo', 'AC', 4, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (119, 'Raimundo Nonato Vera', '109241452', 'Engenheiro Civil', '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (120, 'Leandro Lorran Valle Sarkis', 'A69206-9', 'Arquiteto e Urbanista', '0', 2, 2, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (121, 'Júlio Carvalho Deliberato', '103870148', 'Engenheiro Civil', '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (122, 'Maria Cândida de Araujo Freire', '9002', 'Engenheiro Civil', 'AC', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (123, 'Fernanda Kleckner Parrilha', '8815', 'Engenheira Civil', 'AC', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (124, 'Neyvo Ribeiro Souza da Silva', '2606117956', 'Engenheiro Civil', '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (125, 'Paulo Lopes Parrilha Júnior', '101193785', 'Engenheiro Civil', '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (126, 'Silas Silva de Santana', 'A143809-3', 'Técnologo', '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (127, 'Silvio Rogério da Silva Júnior', '9439', 'Engenheiro Civil', 'AC', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (128, 'Hugo Hiroyuki Tsuchiya Sano', '118538535', 'Engenheiro Civil', '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (129, 'Marina Mesquita de Castro', '71869', 'Arquiteta e Urbanista', 'MG', 2, 2, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (130, 'Luiz Augusto Moraes Neves', '5055', 'Engenheiro Civil', 'AC', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (131, 'Estela Anute dos Santos', 'A316334', 'Arquiteta e Urbanista', '0', 2, 2, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (132, 'José Ronaldo Melo de Oliveira', '7799D', 'Engenheiro Civil', 'CE', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (133, 'Paulino de Almeida Lima Netto', '608370304', 'Engenheiro Civil', '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (134, 'Isadora da Costa Rocha', '9540', 'Arquiteta e Urbanista', 'AM', 2, 2, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (135, 'Haley Márcio V. Boas da Costa', '104439181', 'Engenheiro Civil', '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (136, 'Aleilson da Silva Cordeiro', '107189399', 'Engenheiro Civil', '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (137, 'Sandra Maria Parlote Silva', '1244469', 'Arquiteta e Urbanista', '0', 2, 2, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (138, 'Alex Venicius Aquino da Silva', '100471919', 'Engenheiro Civil', '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (139, 'Darci Rogério do Vale', '4445', 'Engenheiro Civil', 'PA', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (140, 'Francisco Ari da Silveira Júnior', '106596799', 'Engenheiro Civil', '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (141, 'Soad Farias da Franca', 'A5891-2', 'Arquiteta e Urbanista', '0', 2, 2, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (142, 'Ricardo Campelo Esteves', 'A39693-1', 'Arquiteto e Urbanista', '0', 2, 2, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (143, 'Gleizer Rodrigues de Lima', '8329', 'Engenheiro Civil', 'AC', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (144, 'Edmundo Estácio da Silva', '107881632', 'Técnologo', 'AC', 4, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (145, 'Emile Christine de Matos Alencar', '9310', 'Engenheira Civil', 'AC', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (146, 'Sérgio Tsuyoshi Murata', '1571', 'Engenheiro Civil', 'AC', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (147, 'Roberto Cunha Monte', '8591', 'Engenheiro Civil', 'AC', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (148, 'Fabiana Raggi de Sá Sant''ana', 'A27537-9', 'Arquiteta e Urbanista', '0', 2, 2, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (149, 'Fernando Sousa Cardoso', '140619288', 'Engenheiro Civil', '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (150, 'Larsson Diogo Seabra Coelho', '18037', 'Engenheiro Civil', 'GO', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (151, 'Marcelo Ferreira Picinato', '5061570709', 'Engenheiro Civil', 'SP', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (152, 'Célio Sobreira Damasceno', '6510', 'Engenheiro Civil', 'CE', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (153, 'Luciola Maria de Albuquerque Silva', '1602821720', 'Engenheiro Civil', '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (154, 'André Anastácio de Queiroz Neto', '10010057', 'Engenheiro Civil', '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (155, 'Michelle Marcelo Fonsaka', '82026', 'Engenheira Civil', 'PR', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (156, 'José Rodrigues de Lima', '7837', 'Técnologo', 'AC', 4, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (157, 'Yara Moreira Menezes', 'A691992', 'Arquiteta e Urbanista', '0', 2, 2, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (158, 'Ricardo Santos Bártholo', 'A39693-1', 'Arquiteto e Urbanista', '0', 2, 2, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (159, 'Wildyles Disley Lopes Campos', '2168501025', 'Técnologo', '0', 4, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (160, 'Rui Hagmann Bentes', '2813', 'Engenheiro Civil', 'PA', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (161, 'Júlio Roberto Uszacki Júnior', '9066', 'Engenheiro Civil', 'AC', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (162, 'Aurinete Franco Malveira', 'A376094', 'Arquiteta e Urbanista', '0', 2, 2, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (163, 'Gabriel Silva da Costa', 'A51868-9', 'Arquiteto e Urbanista', '0', 2, 2, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (164, 'Ariadne Melo da S. M. Barbosa', '1095', 'Arquiteta e Urbanista', 'RO', 2, 2, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (165, 'Magnus Marcello de Oliveira Martins', '6990', 'Técnologo', 'AC', 4, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (166, 'Roberto Alves de Sá', '101934904', 'Técnologo', '0', 4, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (167, 'Karine Geber de Lima', 'A66829-0', 'Arquiteta e Urbanista', '0', 2, 2, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (168, 'Arythana de Souza Ferraz', 'A74186-8', 'Arquiteta e Urbanista', '0', 2, 2, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (169, 'José Venilson de Carvalho', '6908', 'Tecnologo', 'AC', 4, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (170, 'Fábio Vasconcelos de Souza', '12846', 'Engenheiro Civil', 'CE', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (171, 'Oscar Pereira dos Reis', '1507004753', 'Engenheiro Civil', '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (172, 'Jorge Mardini Sobrinho', '370490', 'Arquiteto e Urbanista', '0', 2, 2, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (173, 'Célio Monteiro da Silva Júnior', '104689609', 'Técnologo', '0', 4, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (174, 'Jairo Costa dos Prazeres', '10825349', 'Engenheiro Civil', '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (175, 'Gerson Roberto Ramos da Silva', '7620', 'Engenheiro Civil', 'AC', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (176, 'Marcus Luiz Pereira Dantas', '10611209', 'Engenheiro Civil', '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (177, 'Nasser Haluane Chaves', '8639', 'Engenheiro Civil', 'AC', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (178, 'Ricardo Meira Eluan', '39485', 'Engenheiro Civil', 'RJ', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (179, 'Lincoln Lima e Silva', '6707', 'Engenheiro Civil', 'AC', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (180, 'Josélia da Silva Alves', 'A12782-5', 'Arquiteta e Urbanista', '0', 2, 2, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (181, 'Ivo Wiciuk Júnior', '100437532', 'Engenheiro Civil', '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (182, 'Jose Siqueira de Figueiredo Neto', '9431', 'Engenheiro Civil', 'AC', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (183, 'Sandra Maria Miranda Cavalcanti', '182525', 'Engenheira Civil', 'SP', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (184, 'Max Rodrigo Knoch', '102820546', 'Engenheiro Civil', '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (185, 'Verônica Vasconcelos de Castro', 'A22400-6', 'Arquiteta e Urbanista', '0', 2, 2, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (186, 'Joaquim Eugenio Bezerra Dias', '104195169', 'Engenheiro Civil', '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (187, 'Paulo Roberto Prezepiorski', '80515', 'Engenheira Civil', 'PR', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (188, 'Álvaro Vicente de Sousa Oliveira', '102050597', 'Engenheira Civil', '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (191, 'José Antonio de Lima', '100457347', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (192, 'Alfredo Renato Pena Braña', 'A71016-4', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (193, 'Raimundo Monteiro da Silva Neto', '7369', NULL, 'AC', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (194, 'Dafnis João Rodrigues Ferreira', '101346930', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (195, 'Leydiane Ferreira Hadad', '976989', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (196, 'Irenice S. Mourão Brandão', '8970', NULL, 'AC', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (197, 'Tamires Menezes de Morais Melo', 'A69204-2', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (198, 'Zeneide da Mota Pinheiro', 'A334901', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (199, 'Átila Pinheiro de Souza', '8016', NULL, 'AC', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (200, 'Sérgio Yoshio Nakamura', '128078', NULL, 'SE', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (201, 'Leandro Braga Vieira', '2204779601', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (202, 'Gustavo Pimentel Moreno', 'A41699-1', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (203, 'Josemar Luis Marcon', '30813', NULL, 'PR', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (204, 'Laura Camila Mamed', '100096158', NULL, 'AC', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (205, 'Joselito José da Nóbrega', '1606065548', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (206, 'Rogério Victor Alves Melo', '109221125', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (207, 'Gilberto Aires Monteiro', '101063890', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (208, 'João Oliveira de Albuquerque', '101442475', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (209, 'Bruno Sarmento Rocha Leal', '2106544863', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (210, 'André Luiz Castro do Nascimento', '109220960', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (211, 'Luiz Alberto Círico', '12265-3', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (212, 'Teófilo Monteiro Lessa Netto', '10078111', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (213, 'Suemi Pontes Hamaguchi', 'A691941', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (214, 'Francisco Ferreira da Silva Furtado Filho', '2438', NULL, 'AC', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (215, 'Edilberto Ferreira Janssen Júnior', 'A752290', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (216, 'Raimundo Estácio da Silva', '9508', NULL, 'AC', 4, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (217, 'Adcleides Araújo da Silva', '106453580', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (218, 'Willyams Moraes de Lima', '106852124', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (219, 'Mário Tadachi Yonekura', '106089676', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (220, 'Marcos Augusto de Oliveira Meireles', '8031', NULL, 'AC', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (221, 'Leandro de Melo Assis', '9633', NULL, 'AC', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (222, 'Marcos Venicio de Oliveira Holanda', '8944', NULL, 'AC', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (223, 'Leandro dos Santos Silva Nascimento', 'A42477-3', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (224, 'José Naldo de Souza Freitas', '100107141', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (225, 'Cristiane Ramos', 'A317934', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (226, 'Willem Assef de Carvalho', 'A728870', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (227, 'Sérgio Murilo Nascimento Mota', '1301904937', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (228, 'Elmar Batista de Lima', '1292', NULL, 'AC', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (229, 'Anderson Vidal de Lima', '9643', NULL, 'AC', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (230, 'Manoel Domingos Alves Neto', '105915599', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (231, 'Marselha Coelho Salomão', 'A59522-5', NULL, '0', 3, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (232, 'Lineu Alves Cavalcante Júnior', '12265', NULL, 'CE', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (233, 'Wellington Viana da Silva', '107189305', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (234, 'Yael da Silva Saraiva', '109239350', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (235, 'Rogério Victor Alves Melo', '109221125', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (236, 'James Chelton Carneiro Lopes', '2663', NULL, 'AC', 4, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (237, 'Renata Favero', '107056-8', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (238, 'Dina Márcia Nascimento Rodrigues', 'A64457-9', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (239, 'Diemerson Monteiro da Silva', '107826941', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (240, 'Luis Carlos de Amorim', '861024126', NULL, 'RJ', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (241, 'Paulo Rogério de Oliveira Chaves', '11765', NULL, 'CE', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (242, 'Odicleia Camara da Costa', '8132', NULL, 'AC', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (243, 'Marcos Venicio de Paiva Souza', '9645', NULL, 'AC', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (244, 'Antonio Marcos Aziz', '20744-6', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (245, 'Frederico Zanin', '108536645', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (246, 'Rafaela Oliveira do Vale', 'A692018', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (247, 'Thiago Rodrigues Gonçalves Caetano', '9196', NULL, 'AC', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (248, 'Flávio Luiz Calixto', '2013060874', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (249, 'Airton Lima de Menezes', '289', NULL, 'MS', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (250, 'Mauro Luiz Neves Nogueira', '48380', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (251, 'Jerônimo Santos Brasil', '8590D', NULL, 'AC', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (252, 'Francisco Carlos Maciel', '1613711921', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (253, 'David Queiroz de Santana', '84255-9', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (254, 'José Assis Benvindo', '101166052', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (255, 'Abdenago Alves Pereira Filho', '3212', NULL, 'CE', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (256, 'Mateus Silva dos Santos', '104103647', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (257, 'José Maria Leão do Amaral', '8981', NULL, 'AC', 4, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (258, 'Tiago Bezerra Frota', 'A71663-4', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (259, 'Narah Gleid Mazzaro Nascimento', '9634', NULL, 'AC', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (260, 'Girlene Lima de Araújo', 'A710253', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (261, 'Antonio Araújo Rodrigues', 'A710180', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (262, 'Eudislei André Nascimento Costa', 'A75060-3', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (263, 'Fernando Pinto de Brito', '8989', NULL, 'AC', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (264, 'Wilton César de J. Sales de Oliveira', '8014', NULL, 'AC', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (265, 'Reginaldo Pereira Siqueira', '110744934', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (266, 'Edunyra Maria das Graças de Magalhães Assef', 'A2529-1', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (267, 'Pettersson Márcio de Souza', '8480D', NULL, 'AC', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (268, 'Davi Luíz Gruhn Damasceno', '9213D', NULL, 'AC', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (269, 'Manoel Peres Bayma Neto', '100437516', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (270, 'David José Tamwing Isihuchi', '107018446', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (271, 'Julio Cesar Fragoso', '2387D', NULL, 'AC', 4, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (272, 'Francisco William Villar de Carvalho', 'A75055-7', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (273, 'Kristiano Tramontim Koch', '102444-7', NULL, 'SC', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (274, 'Francisco de Assis Dantas', '106118188', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (275, 'José Alfredo Vaz de Asevedo', 'A75056-5', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (276, 'Marcelo Sanches de Menezes', '101693249', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (277, 'João Vilarinho Amaral', '16301', NULL, 'PE', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (278, 'Railson Antonio Pontes de Assis', '107189062', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (279, 'Ney Pinheiro de Souza', '102647623', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (280, 'Nélio Alzenir Afonso Alencar', '966', NULL, 'RO', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (281, 'Tadeu Ferreira Castelo', '7946', NULL, 'AC', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (282, 'Ana Carolina Camargo R. do Valle', 'A30101-9', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (283, 'Eurydice Chagas Cruz Neto', '11903', NULL, 'MS', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (284, 'Camilo Lelis de Gouveia', '1412786258', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (285, 'Luiz Expedido Amaro de Freitas', '4038', NULL, 'AC', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (286, 'Aldi Cesar Alódio da Silva', '8327', NULL, 'AC', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (287, 'Marlen Sara de Melo Silva Drumond', '8621', NULL, 'AC', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (288, 'Nilton Luis Bittencourt Silveira', '56101', NULL, 'RJ', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (289, 'Vinicius Otsubo Sanches', '100565476', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (290, 'Regina Lúcia Bezerra Kipper', 'A0834-6', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (291, 'Francisco Everaldo de Souza Ferreira', '9489', NULL, 'AC', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (292, 'Carlos Alberto Coelho Bianco', 'A25786-9', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (293, 'Romel Marques Alves Pereira', '5060524896', NULL, 'SP', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (294, 'José Mauricio Escobari Jimenez', '9556', NULL, 'AC', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (295, 'Augusto Masson Moniz de Assis', '9325', NULL, 'AC', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (296, 'Ary Dionor Viana Rabelo', 'A4570-5', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (297, 'Eliane Siqueira dos Santos', 'A68476-7', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (298, 'José Augusto Benício da Silva', 'A75054-9', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (299, 'Ricardo Emerson Jardim Rodrigues', '901042286', NULL, 'RJ', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (300, 'Moacyr Corsi Junior', 'A34791-4', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (301, 'Maria Daniela Vasconcelos de Freitas', 'A46703-0', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (302, 'José Fernando da Silva Neto', '6856', NULL, 'AC', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (303, 'Antonia Fernades Martins', '7165', NULL, 'AC', 4, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (304, 'Charles da Silva Martins', '100092489', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (305, 'Orleilson Gonçalves Cameli', '401780465', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (306, 'Luiz Antonio Lambert', '148803', NULL, 'SP', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (307, 'Roberto Tufic de Moura Júnior', '113738099', NULL, '0', 4, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (308, 'Rosania Caldeira', '177494', NULL, 'SP', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (309, 'Mauro Carvalho de Mesquita', '106735683', NULL, '0', 4, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (310, 'Leandro Costa de Lima', '108161013', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (311, 'Jair Fernando Campos da Silva', 'A75070-0', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (312, 'Micaelle Maia Coelho', '104208848', NULL, 'AC', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (313, 'Júlio Cesar Maia Freire', '357', NULL, 'AM', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (314, 'Marco Antonio Wanderley Freire', '109391659', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (315, 'Ítalo de Brito Araújo', '139563-7', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (316, 'Alan Pinho da Silva', 'A72897-7', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (317, 'Carlos Drumond do Nascimento Morais', '108160610', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (318, 'Jamison Patrick da Silva Medeiros', '108090019', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (319, 'João Thiago Maciel Marinheiro', '102491224', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (320, 'Washington Luiz de Lima Sousa', 'A72891-8', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (321, 'Adriana Nogueira Lopes Parrilha', '139221-2', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (322, 'Bruno Fabricio Freitas de Araújo', '9540', NULL, 'AC', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (323, 'Ítalo Bruno Nascimento Facundes', 'A73869-7', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (324, 'Edmilson Freitas de Oliveira', '6704', NULL, 'AC', 4, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (325, 'Ariadne Afonso de Castro', 'A44202-0', NULL, 'AC', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (326, 'Josué de Luccas Júnior', '110690460', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (327, 'Makllayne Neves Silva', 'A583090', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (328, 'Michelle Tavares da Silva', '12182-7', NULL, 'AC', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (329, 'Clarindo Antonio Zanotti Júnior', '104327626', NULL, 'AC', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (330, 'Walter Peña Mariscal', 'A10264-4', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (331, 'Alessandro de Oliveira Lima', '5060727375', NULL, 'SP', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (332, 'Keith Wilian Bandeira Macedo', '9619D', NULL, 'AC', 4, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (333, 'Romulo Castro da Silva', '106702254', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (334, 'Fernando Jorge da Silva e Sousa', '101096291', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (335, 'Bruno de Alcantara Mourão', '4445D', NULL, 'RO', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (336, 'Carlos Alberto Dantas da Silva', '8044D', NULL, 'AC', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (337, 'Eduardo Queiroz Yarzon', '1706117485', NULL, 'AC', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (338, 'Mariane Roberta Linhares Dias', 'A470006-6', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (339, 'Gigliane Pereira de Freitas', '7829D', NULL, 'AC', 4, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (340, 'Marla Raquel Cardoso Guimarães', 'A30814-5', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (341, 'Alcerio Antonio de Oliveira', '6413D', NULL, 'AC', 4, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (342, 'José Carlos Martins da Silva', 'A97329-7', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (343, 'Rostenio Ferreira de Souza', '8323D', NULL, 'AC', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (344, 'Leandro Neder de Faro Freire', 'A35148-2', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (353, 'Fernando Henrique Alves Pedrosa', 'A21346-2', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (345, 'Sherlly de Brito Cordeiro', '139166-6', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (346, 'Rodrigo Gonçalves Martins', '112088368', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (347, 'Reginaldo Rodrigues Alves', '9326D', NULL, 'AC', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (348, 'Cleyton de Oliveira Almeida', '100406122', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (349, 'Odir Garcia de Freitas', '6114D-771', NULL, 'PR', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (350, 'Eder Soares Cordeiro', 'A100038-1', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (351, 'Natália Rodrigues Medeiros de Albuquerque', 'A49076-8', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (352, 'Reinard Dudy', '111729602', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (354, 'Andrey da Conceição Reston', '5362D', NULL, 'AM', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (355, 'Doriel Progenio dos Santos', '104313706', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (356, 'Marlúcia Candida de Oliveira Neves', 'A19659-2', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (357, 'Isadora da Costa Rocha Chemim Gonçalves', 'A36143-7', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (358, 'Almir Paiva dos Santos', '101163967', NULL, '0', 4, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (359, 'Sebastião Gregório Alves', '139441-0', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (360, 'Haline de Souza Andrade', 'A75071-9', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (361, 'Paulino Henrique Dutra da Cruz', '2475D', NULL, 'PB', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (362, 'Luciano Soares Lima', '141391-0', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (363, 'Francisco Evaristo de Freitas', '4373D', NULL, 'AC', 4, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (364, 'Ana Paula Bizare Mustafa', 'A1135147', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (365, 'Gicélia Viana da Silva', '8320D', NULL, 'AC', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (366, 'Teresinha da Silva Melo', 'A168377-2', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (367, 'André Luiz Vasconcelos de Andrade', '108108503', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (368, 'Ricardo Moreira Carneiro', '1305252365', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (369, 'Leonardo Garcia Bastos', '000010905D', NULL, 'DF', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (370, 'Ney Roberto da Rocha Brana', '102057737', NULL, '0', 4, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (371, 'Edgard Bokel Martins Costa', '2726D', NULL, 'DF', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (372, 'Dário Rogerio do Vale', '106555227', NULL, 'AC', 4, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (373, 'Eduardo Nunes Vieira', 'A6562-5', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (374, 'Fernanda Fecury de Mello Feres', 'A1019643', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (375, 'Marco Antonio Rodrigues', '2567D', NULL, 'AC', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (376, 'Gabriel Issac do Vale Israel', '108105407', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (377, 'Arides Rodrigues', '851053670D', NULL, 'RJ', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (378, 'Gleilce Andrade Araujo de Lima', '100102298', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (379, 'Márcio Alberto Clementino da Silva', '114338531', NULL, '0', 4, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (380, 'Cleriston Freire de Andrade', 'A75062', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (381, 'Isla Samara Silva Madeiros', '121788-7', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (382, 'Sônia Maria de Souza Santos', '9447D', NULL, 'AC', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (383, 'Wilma Assef de Carvalho', '9440D', NULL, 'AC', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (384, 'Jorge Balica Monteiro', '100593585', NULL, 'AC', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (385, 'Emiliana de Souza Dantas', 'A109394', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (386, 'Fabio Alexandre Modesto', '5061375297', NULL, 'SP', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (387, 'Marcelo Negreiros de Souza', '110943341', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (388, 'Valdenilson da Silva França', '139563-7', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (389, 'Vanessa Candine Silva Oliveira Ferreira', 'A71020-2', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (390, 'Ernani Henrique dos Santos Júnior', 'A16244-2', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (391, 'José Ricardo Gonçalves', '7996D', NULL, 'AC', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (392, 'Márcio Andrade Lucena de Araújo', 'A48232-3', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (393, 'Reginaldo Rodrigues da Silva', 'A1062689', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (394, 'Eliseyna Stefane Oliveira dos Santos', 'A1472992', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (395, 'Marcos Venicius de Paiva Souza', '109248716', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (396, 'Wilton Ferreira Azevedo Junior', '2301867218', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (397, 'George Henrique Moreira Bezerra', '105031003', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (398, 'Eduardo Amorim da Silva Filho', '2111695513', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (399, 'Elson Pereira Magalhães', 'A171905', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (400, 'Antonio Rodrigues Barbosa Neto', '112088384', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (401, 'Lorena Mara Oliveira do Nascimento', '141970/6', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (402, 'Ketlyn Fernanda Reda Oliveira', 'A69207-7', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (403, 'Victor Hugo Sestito Salomão', 'A1010174', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (404, 'Jimmy Monnerat Amorim', '112088520', NULL, 'AC', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (405, 'Nátalia  Rodrigues Medeiros', '2007121385', NULL, 'RJ', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (626, 'David dos Santos Telles', '102045453', NULL, '0', 4, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (406, 'Natália Santiago Albuquerque', '140654-0', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (407, 'Israel de Araújo Sousa', 'A111056', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (408, 'Ronald Camargo Suzuki', '107513846', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (409, 'Fabiano de Araújo Cruz', '109240952', NULL, 'AC', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (410, 'Andréa Farias Nobre', 'A108357', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (411, 'Claudia Maria Pinto Ribeiro', 'A17560-9', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (412, 'Valcimar Costa de Andrade', '2611306060', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (413, 'Dhefle Kaiã Sousa Macêdo', 'A100372', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (414, 'Renato Sousa Sol', '1400637104', NULL, 'MG', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (415, 'João Ricardo Ferreira da Silva', '112088317', NULL, 'AC', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (416, 'Sebastião Gregorio Alves', '100804837', NULL, '0', 4, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (417, 'Janara Alexandre da Silva Vasconcelos', '10718762', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (418, 'Esdra Aquila Gama de Sousa', '11208853-8', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (419, 'Júlio Augusto Pinheiro Araújo', '104457929', NULL, 'AC', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (420, 'Joanita da Silva Santos', 'A69158-5', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (421, 'Luis Antonio de Luca Neto', '100454496', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (422, 'Edgard de Oliveira Neto', '111513987', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (423, 'Malcolm Teles de Oliveira', 'A950718', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (424, 'Ricardo Laurentino Vasconcelos', '68705', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (425, 'Wilson de Andrade Lima Filho', '106634526', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (426, 'Ariel de Melo Nicácio Rodrigues', 'A105416-3', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (427, 'Renato Souza Santos', '108146723', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (428, 'Kenned Kaccio Rodrigues Constantino', 'A117360', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (429, 'Moises Coelho da Costa', '7972D', NULL, 'AC', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (430, 'Geremias de Souza Frota', 'A104879-1', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (431, 'João Setsuo Watanbe', '101502702', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (432, 'Rosinete de Fátima Moreto', 'A156443-9', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (433, 'Germano Pimentel Farias', '113436050', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (434, 'Cristiana Monteiro Ferreira de Barros', '135390-0', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (435, 'Sara Sena Castro', '107188570', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (436, 'Francisca Katiuscia Oliveira de Carvalho', '100798322', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (437, 'Alexandre Cicero Araújo Beiruth', 'A1061160', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (438, 'Priscyla Oriane Brasileiro', 'A1075292', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (439, 'Reinaldo do Nascimento Morais', '2300024241', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (440, 'Samilca da Silva França', 'A1128302', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (441, 'Raphael Vitório Nóbrega Balbino', '9108D', NULL, 'AC', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (442, 'José Teixeira de Lima Junior', '109240235', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (443, 'Kamila Gonçalves de Sousa', 'A1106708', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (444, 'Marilson Melo Cavalcante', 'A1057308', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (445, 'Áfra Silva Brito', '1512153', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (446, 'Osvaldo Canizares', '282804', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (447, 'Thales Silva de Melo', '113208561', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (448, 'Ana Paula Onofre Barros', '112088414', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (449, 'André Luiz Mouta Rocha', 'A108162-4', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (450, 'Fernando Pinto de Brito Borba', '100987516', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (451, 'João Paulo de Mendonça Souza', '112088635', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (452, 'WEBBER XAVIER D''AVILA LUCENA', '112863825', NULL, 'AC', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (453, 'Lucio Eugenio de Souza Consales', '101000553', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (454, 'Cleudo Malveira Moura', '107018527', NULL, 'AC', 4, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (455, 'Bruno de Franco', 'A2245-4', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (456, 'Wilson Wieck', '2603512838', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (457, 'Kiefer Roberto Cavalcante Lima', '106088661', NULL, '0', 4, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (458, 'Nonato da Silva Brito', '1485369', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (459, 'Thallis Melo de Carvalho', 'A1173642', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (781, 'Carlos Henrique da Silva Alves', '182851-7', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (460, 'Francisco das Chagas Costa da Silva', 'A1110276', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (461, 'Rubens Aviz', 'A3911-0', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (462, 'Felipe Montenegro Grieser Leal de Souza', '608734870', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (463, 'João Paulo  Feitosa Couto', '112088309', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (464, 'Daniel Almeida da Luz', '108160777', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (465, 'Walyson Kadu Santos de Melo', '112088643', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (466, 'Antonio José Alexandre de Araújo', '101911742', NULL, '0', 4, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (467, 'Antônio Flores de Queiroz', '106469606', NULL, '0', 4, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (468, 'Fábio Magalhães Fontenele', 'A1088653', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (469, 'Maria Gabrielle Martins Miguéis', 'A71028-8', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (470, 'Eudes Moreira da Costa', '112088554', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (471, 'Maria Floraci Machado Domingues', 'A75064-6', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (472, 'Marcelo Renaux Willer', '246522', NULL, 'SP', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (473, 'Adriano Olga de Souza Bertoncello', '2601595787', NULL, 'SP', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (474, 'Odiney Augusto de Andrade', '1300170077', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (475, 'João Batista Miguel', '701789697', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (476, 'Márlen Sara de Melo Silva Drumond', '100107117', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (477, 'Arthur de Oliveira Viana Neto', '106080717', NULL, '0', 0, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (478, 'Ciro Augusto de Macedo Rebello de Souza', 'A260177', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (479, 'Leandro Rocha dos Santos', 'A750727', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (480, 'Carolini Jucá Ramos', 'A1090259', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (481, 'Aluizio Antonio Veras', '6611D', NULL, 'AC', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (482, 'José Augusto Leão Camilo', '7564D', NULL, 'GO', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (483, 'Daniela Bezerra Kipper', 'A290297', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (484, 'Diego Soares do Nascimento', '112088708', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (485, 'Erika Velasco Rocha', '1355171', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (486, 'Naína Maria Cordeiro Dantas', 'A1100548', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (487, 'Samara Raquel D', '1600727638', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (488, 'Marcel Magalhães Lima', '112088325', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (489, 'Breno José Silva e Silva', 'A1003240', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (490, 'Luciana Meireles Araujo Moreira', 'A1547941', NULL, 'AC', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (491, 'Alan Callid Damasceno Oliveira', '107188953', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (492, 'Fenando Antonio Farinazzo', 'A166766', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (493, 'Antônio Campos Gonzaga', '1408690128', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (494, 'Pablo Ney de Melo Queiroz', 'A118423-7', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (495, 'Josevaldo de Souza Meira', '1301168220', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (496, 'Marco Antonio Otsubo Sanchez', '106469819', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (497, 'José Maria Batista Saraiva', '102666962', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (498, 'Claudenor Zopone Junior', '601680959', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (499, 'Leticia Doering Shimabukudo', '5063875437', NULL, 'SP', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (500, 'Suena da Costa Ferreira', '103996761', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (650, 'Mirian Costa de Mattos Leite', 'A1664271', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (501, 'Cleverson Klaus', 'A129238-2', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (502, 'Antônio Pinto Carneiro', '604484089', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (503, 'Aluildo de M. Oliveira', '106468898', NULL, '0', 4, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (504, 'Isadora de Farias Parreira', 'A129227-7', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (505, 'Diego Caetano de Freitas', '5069254262', NULL, 'SP', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (506, 'Wellyson da Silva Lima', 'A1068717', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (507, 'Tomas de Aquino Pereira Neto', '1507004796', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (508, 'Walmiro Mendes de Jesus Filho', '1606727', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (509, 'Igor Rafael Soares Pires', 'A1228838', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (510, 'José Carlos da Rocha', '500939233', NULL, 'BA', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (511, 'Rogério Vasconcelos de Souza', 'A29399-7', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (512, 'Rafael Rossi da Rocha', '2509948709', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (513, 'Valdelan Lopes da Silva', 'A1171836', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (514, 'Débora Nogueira Lopes Parrilha', 'A2234289', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (515, 'Esdras Aquila Gama de Sousa', '112088538', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (516, 'Renato Luiz Perez Malicheski', '1702778240', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (517, 'Paulo César Barbosa de Toledo Lourenço', 'A249645', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (518, 'Alam Moacyr Lucena e Souza', 'A1111280', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (519, 'Rafael Gaspar Fernandes', '11449978', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (520, 'Rafael Vasconcelos Eluan', 'A1003313', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (521, 'Expedito Costa Cavalcante', '100820263', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (522, 'Igor da Silva Barbosa', 'A1085093', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (523, 'Mahatma Soares de Moura Silva', '1630350', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (524, 'Renan César Nogueira Ferraz', 'A109832-2', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (525, 'Whendryckson Werbster de Lima Wanderley', 'A721182', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (526, 'Victor Hugo Rodrigues Barbosa', '109217691', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (527, 'Simon Tupac Alvarez Catalan', '109217039', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (528, 'Afrodite Lessa Pinheiro do Vale', 'A750573', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (529, 'Antonio Carlos Lapa Bezerra', '402042727', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (530, 'Raida Machado da Silva Azevedo', 'A1003402', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (531, 'Jorge Almeida de Mesquita', 'A122202', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (532, 'Lucas Jalul Araújo de Alexandria', '114298998', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (533, 'Jarlene de Souza Ramirez', 'A146968-1', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (534, 'Célio Monteiro da Silva Júnior', '104689609', NULL, '0', 4, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (535, 'Gerson Pereira Monteiro', '101120540', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (536, 'Carlos Henrique Dias Lima', 'A1033190', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (537, 'André Lucas Rodrigues de Araújo', 'A126400-1', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (538, 'Thais Regina Souto Pessoa', 'A1212931', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (539, 'Geiza Gabriela de Araújo Negreiros Sasai', 'A891819', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (540, 'Elizângela dos Santos Maciel', 'A939153', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (541, 'Antônio Ealder Macedo Luna', '101841620', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (542, 'Emerson Pinheiro Valentim Lima', '114309302', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (543, 'Fernando Costa de Carvalho', 'A1182137', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (544, 'Márcio Henrique Rodrigues de Oliveira', '101911823', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (545, 'Antônio Roberto Rocha Moreira', '108267504', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (546, 'Lisangela Pazinatto', 'A520756', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (547, 'Paulo Roberto Cavalcanti', '2107965340', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (548, 'Marcos Lourenço Bezerra da Silva', '100437540', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (549, 'Dériki Darvin Moura Lima', '1606670', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (550, 'Railda Machado da Silva Azevedo', 'A1003402', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (551, 'Giancarlo Varillas Balbuena', '114286965', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (552, 'Anselmo Luiz dos Santos', '1200036859', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (553, 'Glaicy Kelly Machado Gonçalves', '1306022223', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (554, 'Jaqueline Lino de Araújo Cardoso', 'A335800', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (555, 'Cezar Mecozzi Tente', 'A630187', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (556, 'Bruna Costa de Sousa', '1630245', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (557, 'Jorge Luiz dos Santos Silveira', 'A746835', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (558, 'Laryssa Félix dos Santos', '1498541', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (559, 'Cherlany Karen de Souza Rocha', '1568094', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (560, 'Thiago Tamotsu Yonekura', '114375216', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (561, 'Geraldo Cesar Ferreira', 'A116696-4', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (562, 'Carlos Afonso Cypriano dos Santos', '114120447', NULL, '0', 4, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (563, 'Abrahaão Henrique Ribeiro', 'A123498-6', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (564, 'Danilo Rogério do Vale Júnior', '100151558', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (565, 'Radames da Silva Ferreira', '111708451', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (566, 'Vanderlei Freitas Valente Junior', '114286000', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (567, 'Jayr dos Reis Souza Meira Filho', 'A128657-9', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (568, 'Melina Wentz da Silva', 'A1682555', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (569, 'Camila Jorge Filipini', 'A385158', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (570, 'TEDER SEIXAS DE CARVALHO', 'A290114', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (571, 'Eduardo Augusto de Holanda e Souza', '1512657263', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (572, 'Fernando Silva de Brito', '114299170', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (573, 'Welldem Derze do Nascimento', 'A1009923', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (574, 'Reuel Barbosa Moraes da Costa', '112088546', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (575, 'Yuri Vieira Araújo', 'A2329565', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (576, 'Heber Feitosa dos Santos', '1307552099', NULL, 'AC', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (577, 'CLAUDIO RODRIGUES MELO', '78591', NULL, 'MG', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (578, 'PAULA KATIUSCIA DOSSA', 'A923508', NULL, 'AC', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (579, 'Iuçara Andrade da Costa Souza', 'A121339-3', NULL, 'AC', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (580, 'EWERTON NERI DE ARAUJO', 'A71026-1', NULL, 'AC', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (581, 'ANA LARA BESERRA BRAGA', '160646-8', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (582, 'Gilberto Lucas de Oliveira', '102491941', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (583, 'Lucas Almeida Ribeiro', 'A1317776', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (584, 'Cleber Fernando Rodrigues de Souza', '114378738', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (585, 'CRISTIANE SZILAGYU SALDANHA', '151221-8', NULL, 'AC', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (586, 'Celio Monteiro  da silva Junior', '104689609', NULL, 'AC', 4, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (587, 'Wédimo Ribeiro Silva', 'A11966-7', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (588, 'Luiz Felipe Diógenes de Souza', 'A1220535', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (589, 'João Paulo Arruda Rangel de Lima', '1769723', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (590, 'Paulo Renato Noranha Dantas', 'A1068911', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (591, 'Renato Barros Pinheiro', '1688170', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (592, 'Jeferson Augusto Souza e Souza', '506653889', NULL, '0', 4, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (593, 'Kelmy Aguiar Chagas', '111604680', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (594, 'Oginey Ripardo da Rocha', '113207786', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (595, 'Tatiane Barbosa dos Santos', '1752871', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (596, 'Lucas José de Andrade Lopes', 'A1086782', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (597, 'Daniel da Rocha Borges', 'A1317830', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (598, 'Jhonas Manoel Moreira Félix', 'A138518-6', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (599, 'Thalyta França dos Santos', 'A920584', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (600, 'João Pedro Mesquita Lemos Gomes', 'A147313-1', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (601, 'Dandara Cristtinny Brito Lima', 'A69205', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (602, 'Sérgio Murilo Rosa', '2508259110', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (603, 'Iuçara Andrade da Costa Souza', 'A1213393', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (604, 'Thabata Joerke Ansiliero', '1858696', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (605, 'Anderson Rodrigo Pereira', '2613528460', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (606, 'Francildo Chaves da Silva', '105503169', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (607, 'Bruno Souza Ferreira', 'A148657-8', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (608, 'Joaquim Ferreira do Nascimento Júnior', '1412593069', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (609, 'Carlos Takashi Sasai', '53.155D', NULL, 'SP', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (610, 'Cherlany Karen de Souza Rocha', 'A1333143', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (611, 'Mendelson Mendonça da Cunha', '106113534', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (612, 'Vinicius Soligo', 'A633119', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (613, 'Cinthya de Almeida e Alves Santos', '1004630956', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (614, 'Thiago Araújo de Almeida', 'A1240315', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (615, 'Humberto José Soares Hadad', '1512447765', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (616, 'Adrianne Filgueira de Sousa', 'A1259040', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (617, 'Uyara Diniz de Almeida', 'A1022229', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (618, 'Jean Assen Felix Júnior', 'A1147676', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (619, 'Aldekemes Pereira Rodrigues', '175261-8', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (620, 'Lucas Felix de Aquino', 'A124234-2', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (621, 'Cássia Faial Pontes Hadad', '114303231', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (622, 'Cássio Ferreira da Rocha', '114066396', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (623, 'Mauro Sérgio Souza de Freitas', '107472635', NULL, '0', 4, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (624, 'Gicélia Viana da Silva Medici Aguiar', '101911785', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (625, 'José Carlos Martins da Silva', 'A973297', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (627, 'Laegila Neves da Silva da Vitória', '813350247', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (628, 'Fernando Branã Mendonça', '189165-0', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (629, 'Laura Alice Cavalcante de Menezes', 'A1335715', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (630, 'Judeilson Ferreira de Oliveira', '107824256', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (631, 'Henrique Anderson Martins', '1702056767', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (632, 'Márcia Cristina Lessa de Medeiros', 'A146933-9', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (633, 'Dayane Melissa Andrade', '112088295', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (634, 'Lauro de Paula Cardoso', '1568175', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (635, 'Tauan Machado Craveio', 'A1463616', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (636, 'Arisson Silva e Souza', 'A123625-3', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (637, 'Hemilly Cristiele Gondim da Silva', 'A1351729', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (638, 'Jancarlos Albuquerque Monteiro', 'A140611-6', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (639, 'Cristina Albuquerque da Silva França', '1858467', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (640, 'Michele Neri Magalhães de Carvalho', 'A1727648', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (641, 'Kennedy Silva de Lima', '115555765', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (642, 'Nirlandia Bezerra Barbosa Ramalho', '1723618', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (643, 'Leonardo Neder de Faro Freire', 'A351482', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (644, 'Jozilda Pereira Paiva', 'A249181', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (645, 'Vladimir Câmara Tomas', '5060725583', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (646, 'Emmanoelly Aguiar Ferreira', 'A710296', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (647, 'Radamés Queiroz de Souza', '1858653', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (648, 'André Luiz Dias Fernandes', 'A1080903', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (649, 'Rita de Cássia Barros Esteves', 'A123160', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (651, 'Alessandro Crispim Macedo', '2306376069', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (652, 'Andreza Rezende Amaral Macedo', '4386D', NULL, 'RO', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (653, 'Larissa Magalhães Figueiredo', '172359-6', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (654, 'Tatie de Marchi Martins', 'A114758-7', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (655, 'Gabriel Alcioly Assis Vaglieri', '11688102', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (656, 'Daniel Caseiro', '2609322402', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (657, 'Otávio Augusto Leite da Rocha', 'A41729-7', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (658, 'Aliny Karol Silva da Cruz', '185841-6', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (659, 'Kim Robson Rodrigues da Silva', '109240154', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (660, 'Ezio Christian Deda de Araújo', 'A31757-8', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (661, 'Lucas Barbosa da Silva', '196476-3', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (662, 'Geovany Souza de Amorim', 'A148647', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (663, 'Tássia da Silva Lima', '189187-1', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (664, 'Kerolayne Lima Aguilar', '168808-1', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (665, 'José Orlando Miranda', '1105140890', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (666, 'Bruno Barbieri Parreira', 'A58169-0', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (667, 'Andressa Christini Nogueira Parreira', '199228-7', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (668, 'Liliana Carloni Camargo', 'A106813-0', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (669, 'Josué de Luccas Júnior', '110690460', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (670, 'Rogerio Alves da Silva', '1507471173', NULL, 'PA', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (671, 'Kenneth da Silva Lima', 'A1722115', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (672, 'Marcela Leite Torrelio', 'A59786-4', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (673, 'Gabriel Barbosa Morais da Costa', '11436678', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (674, 'Leidiana Moraes Junqueira', 'A134076-0', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (675, 'Thiago Alves Soares', 'A138480-5', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (676, 'Thiago Vieira de Lima', '116643269', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (677, 'Victor Júnior da Silva Jovino', 'A1718428', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (678, 'Ítalo Almeida Lopes', '114371121', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (679, 'Luiz Guilherme de Oliveira Ferrez', '115554750', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (680, 'Mário de Almeida Martins', '1506868665', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (681, 'Aglaia Cerri', 'A89336-6', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (682, 'Joaquim Monteiro Silva', '717530035', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (683, 'Alvaro Miguel Rocha Soler', '107188988', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (684, 'Wilians Montefusco da Cruz', '108232948', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (685, 'Neyldo Franklin Carlos de Assis', '106695363', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (686, 'Saulo Aguiar da Silva', '116522933', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (687, 'Danubia Arruda Silva', 'A60410-0', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (688, 'Rejane Damaris Oliveira da Silva', 'A69202-6', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (689, 'Rafael Maçaneiro de Oliveira', '164958-2', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (690, 'José Cláudio Ferreira', '2611457115', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (691, 'Nathália Menezes Carlos', 'A1663976', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (692, 'Jorge Ribeiro da Silva', '156816-7', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (693, 'Max Mendes Mello', 'A16754-1', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (694, 'Tadeu Victor Salvatierra C. Figueiredo', '11555606', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (695, 'Hernandes Sales Guerra Júnior', '109221656', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (696, 'Arnaldo Alves Cacela Filho', '185844', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (697, 'Raysa Barbosa dos Santos de Souza', 'A115695', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (698, 'Hosana Feijo da Silva', '199239-2', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (699, 'Kellvys Wanderson Frota Lima', 'A136188', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (700, 'Daniel de Araújo', 'A139591-2', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (701, 'Geilson Pessoa Chaves', '1891669', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (702, 'Vitor de Oliveira Peres', 'A1158775', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (703, 'André Luiz Barreiros de Lima', '115555072', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (704, 'João Carlos Guimarães da Silva', 'A1865897', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (705, 'Israel Costa Souza', '206353', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (706, 'Leovânia de Oliveira Moraes Melo', 'A1267469', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (949, 'Farney Gomes de Araújo', 'A1471759', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (707, 'Francisca Gabrielle Freire Farias', '199236-8', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (708, 'Ernande Gueulle Fonseca da Silva', '199235', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (709, 'Cláudio Vendruscolo', '2311575392', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (710, 'Marcelo Henrique da Silva Monteiro', 'A2358255', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (711, 'Alessandra de Aguiar Lopes', '189155-3', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (712, 'Italo Vinicius Saraiva Lopes', 'A1672665', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (713, 'Maysa de Mesquita Pinheiro', '172360', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (714, 'William Ramon Santana Arruda Moreira', '199259-7', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (715, 'Raquel Soares Cordeiro', '196486', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (716, 'José Batista Félix Júnior', 'A160302-7', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (717, 'Luanne Caroline Silva Oliveira', '199245-7', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (718, 'Maria Alcineyde Melo de Lima Farias', '2006455362', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (719, 'Luan Maia Machado', '1752758', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (720, 'Victor Feitoza Monnerat', '189191', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (721, 'Francisco Ricardo Melo de Andrande', '8022/D', NULL, 'CE', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (722, 'Solange Maria Malaquias Carvalho', '199253-8', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (723, 'Gabriel Bastos Nardino', '117519952', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (724, 'Kethelin Monteiro Rocha', 'A125882-6', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (725, 'Raulino Sanchez', '53439', NULL, 'SP', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (726, 'Ana Paula de Oliveira', '107876671', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (727, 'Claúdio de Lima Silva', '1603811214', NULL, 'BA', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (728, 'Jaíne Melo de Souza', '196467-4', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (729, 'Tiago Teixeira Bernardo', '116651121', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (730, 'Haley Márcio Vilas Boas da Costa', '104439181', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (731, 'Diana Aduviri Correia', '189163-4', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (732, 'Jhenifer Vale da Silva Bezerra', 'A137675-6', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (733, 'Josan Barbosa de Sousa', 'A1866028', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (734, 'Ana Cristina da Costa Casas', '199225-2', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (735, 'Marco Antônio Munhos Salvador', '260323241', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (736, 'Leonardo de Moura Naves e Souza', '1004429223', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (737, 'Lorena Vidal Calid', '146197-4', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (738, 'Illa Agnes Freitas Cordeiro', '115553312', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (739, 'Patrícia dos Santos Ferreira Costa', '117987425', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (740, 'Elenildo Correia da Costa', 'A1837621', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (741, 'Kayo da Silva Firmino', '117703060', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (742, 'Giovani Varillas Balbuena', '115555897', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (743, 'Maria de Lourdes Catão Neri', '206387-5', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (744, 'Fernanda Teixeira Cordeiro Freire', 'A1703153', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (745, 'João Batista da Rocha Vieira', 'A1837419', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (746, 'Leomar Santiago Barbosa', 'A1871501', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (747, 'André Luiz Barreiros de Almeida', '115555072', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (748, 'Juliana Maria de Almeida Lima', 'A131574-9', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (749, 'Tariny Queiroz Valer', '196491-7', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (750, 'Diego Carvalho Ribeiro', '199234-1', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (751, 'Kedna Daiane Cavalcante da Silva', 'A1898817', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (752, 'João Batista dos Santos Júnior', 'A142983-3', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (753, 'Marcelo Yan Macêdo Damasceno', '21574D', NULL, 'AC', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (754, 'Walter Alberto Xavier', '1411837851', NULL, 'MG', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (755, 'Ismael Costa Souza', 'A1672835', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (756, 'Fredy Bader Pinheiro', 'A161609-9', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (757, 'Gabriela Renata Salim Ribeiro P. Malheiro', '185851-3', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (758, 'Tales Rhauê da Silva Reis', 'A1701355', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (759, 'Francieli Kalline de Lima Lopes', '196462-3', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (760, 'André Italiano de Albuquerque', '112088392', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (761, 'Januaria Esch Guimarães', 'A38149-7', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (762, 'Carlos Wender Lima de Souza', 'A154825-5', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (763, 'Vanderson Nobre de Souza Rodrigues', 'A121856-5', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (764, 'Diego da Silva Firmino', 'A153076-3', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (765, 'Caroline Teles Gomes Lopes', 'A51403-9', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (766, 'Mário Gustavo da Silva Arlas', '513316361', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (767, 'Yuri Muniz e Silva', '11555511', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (768, 'Débora Araújo de Lima', 'A172117-8', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (769, 'David Dias Martins Filho', '115555323', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (770, 'Larissa Almeida Silva', '117520250', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (771, 'Marcelo da Veiga Santos', 'A72794-6', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (772, 'Billy Mateus Araújo Reis', 'A148640-3', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (773, 'Laísa Samara Campos Soares Lessa', '108160858', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (774, 'Gaudêncio Carneiro de Lima Neto', 'A91220-4', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (775, 'Pollyana Brenda do Vale Braga', 'A145146-4', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (776, 'Paula Cristina França Matias da Cruz', 'A39549-8', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (777, 'Silenio Martins Camargo', 'A22573-8', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (778, 'Wilton Soares de Brito Silva', 'A169855-9', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (779, 'Gustavo Henrique Nunes Ferraz Costa', '118559770', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (780, 'Daniel Marcos Silva dos Santos', '2063271', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (782, 'Cláudia Maria Ayres Yassuda', 'A16738-0', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (783, 'Christian de Brito Rodriguez', 'A127840-1', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (784, 'Wandressa Silveira Menini', 'A159994-1', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (785, 'Alan Carlos Nascimento Nunes', 'A156468-4', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (786, 'Sham Raynne Freitas Souza e Silva', 'A1577840', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (787, 'Mayanne Rodrigues de Sousa', 'A156454-4', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (788, 'Cezario Ferreira Barbosa Júnior', '2408761328', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (789, 'Monique Socorro da Silva Soares', 'A72931-0', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (790, 'Thiago Rezende Dantas', '114380490', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (791, 'Alan Carlos Costa de Miranda', '199224-4', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (792, 'Gerffyrson Jhon de Souza Soares', '117864960', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (793, 'Erik Afonso Gurgel de Andrade', '108125386', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (794, 'Bruno da Cunha Nogueira', '218486-9', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (795, 'Lane Sales de Oliveira', 'A160094', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (796, 'Danuza Janara de Albuquerque', '218494-0', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (797, 'Jéssika Silva de Albuquerque', 'A152447-0', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (798, 'Fernanda Carnevali Furtado de Medeiros', 'A107299-4', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (799, 'Lucas Santos Guerra', 'A2419700', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (800, 'Domingos do Monte - CREA 16452992287', '.', NULL, '0', 4, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (801, 'Natanael Cavalcante Gomes', 'A170611', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (802, 'Antonio José Feitosa da Mota', '224386-5', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (803, 'Williyan Fernandes Dias', 'A157735-2', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (804, 'Laize Liberato Cabral', 'A167561-3', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (805, 'Raielly Larissa Duarte de Souza', '224428-4', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (806, 'Victor Mateus de Araújo Silva', '115556010', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (807, 'Luiz Sousa dos Reis Júnior', 'A123443-9', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (808, 'Caroline Castro Lopes', 'A1249010', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (809, 'Jimena Rosana Aguilar Ameneiros', '10603104', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (810, 'Vanessa de Albuquerque Campos Soares', 'A153792', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (811, 'Renê Sarkis Freire', '151239', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (812, 'Breno de Almeida Melo', '118554603', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (813, 'Victor de Freitas Rodrigues', '117521302', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (814, 'Simone Pesth', 'A123928-7', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (815, 'Manoel Pinheiro de Brito Neto', '106508571', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (816, 'Adaias Oliveira de Araújo', '107188066', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (817, 'Hilla Portela Souza', 'A139736-2', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (818, 'Luciana Oliveira Brito', '117936391', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (819, 'Victorhugo de Souza Lima', '119201682', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (820, 'Nádja Natasha Araújo da Silva', 'A124745', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (821, 'Lucas Martins Ferrajam', '1018749373', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (822, 'Marcos Henrique Sanches', '107189127', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (823, 'Vinicius Beserra Bennesby', 'A1848194', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (824, 'Pietra de Oliveira Araújo Ribera', 'A133108-6', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (825, 'Nelson Ribeiro Neto', 'A124619-4', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (826, 'RAFAEL DE SOUZA COSTA', 'A2413248', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (827, 'Antonio Marcos Costa da SIlva', 'A163985-4', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (828, 'Kaio Vinicius dos Santos Braga', 'A167549-4', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (829, 'Alisson Feitoza Azevedo', '118951955', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (830, 'Madson Willander Melo de Sá', 'A1950550', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (831, 'Steff Lima de Souza', '118555030', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (832, 'José Fábio Maia Filho', '109232720', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (833, 'Celso Almeida Miranda', '115555552', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (834, 'Charbel de Albuquerque Xavier', 'A1875922', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (835, 'Fernanda Martins Farinazo', 'A41064', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (836, 'Caira Lorena Siqueira Rocha', 'A163745-2', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (837, 'Everson Rodrigo da Rosa', 'A183426-6', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (838, 'Carlos Alexandre Franco Gonçalves', 'A30050-0', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (839, 'Ícaro Oliveira Soares de Assis', '118575430', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (840, 'Antonio Delto da Silva Sampaio', '119471647', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (841, 'Letícia Silva Martins', '1512358', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (842, 'Pedro Augusto Silva de Oliveira', '115555307', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (843, 'Fabiano Lima Santiago de Oliveira', '244116', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (844, 'Farly Lima Santiago de Oliveira', 'A1851616', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (845, 'Leny Mara da Silva Antunes', 'A1486551', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (846, 'Ian Soares de Oliveira Gomes', 'A1691180', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (847, 'William dos Santos', '2885596481', NULL, '0', 4, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (848, 'Otávio da Silva Costa', '109072898', NULL, '0', 4, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (849, 'Samyr Braga Sales', '600222608', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (850, 'Karoline Andrade Costa Castro', 'A1705997', NULL, '0', 0, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (851, 'Jaila Ribeiro Duck', 'A1071459', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (852, 'Huggo Raphael Barbosa', 'A1663518', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (853, 'Johnatan de Souza Lima', 'A1906399', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (854, 'Rodrigo Ribeiro da Silva', '2637709235', NULL, '0', 4, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (855, 'Thales Henrique de Oliveira Guedes', 'A1838741', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (856, 'Paulo Henrique Queiroz da Rocha', 'A1453572', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (857, 'João Felipe Ferreira Purita', '2618456726', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (858, 'Eletrid da Silva Vieira', '2607088895', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (859, 'Matheus Franklin Medeiros', '1707147620', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (860, 'Evaldo Cunha de Albuquerque', '106519603', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (861, 'Artur Souza da Silva - CREA: 75825724249', '.', NULL, '0', 4, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (862, 'Otávio Passarelli', '1705481612', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (863, 'Adelar José Piana Júnior', '718744837', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (864, 'João Batista Gomes de Oliveira', '106135171', NULL, '0', 4, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (865, 'Renata Carvalho Souza', '118960636', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (866, 'Marina Mesquita Costa', '118590030', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (867, 'Nailton da Silva Magalhães', '118861107', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (868, 'Ettore Carlo Scuderi', '1406295809', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (869, 'Ulderico Queiroz Júnior', 'A71021', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (870, 'Paulo de Tarso Avila Paz', '102238162', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (871, 'Saul da Silva Lima', '118313380', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (872, 'Kleiton Júnior Colombo', '2063719', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (873, 'Gislaine Peixoto Lima', 'A2389304', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (874, 'Márcio Silva de Abreu Júnior', '1413163114', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (875, 'Luis Arthur de Souza Yunes', 'A2406918', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (876, 'Anna Paula Costa', 'A1847350', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (877, 'Kaliton José Barbosa e Silva', '115555854', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (878, 'Juan Soares Rodrigues', '2314796926', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (879, 'Fernanda Valladão', '119174960', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (880, 'Juliana Nunes de Gusmão Mendes', 'A1643720', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (881, 'Pedro Vinicius Araújo Vieira', '118177257', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (882, 'Wilson Machado de Castro Júnior', '2306511462', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (883, 'Olivia Nobre da Rocha', '2441667', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (884, 'Sandra Pesth', 'A1239295', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (885, 'Saulo Barroso Cavalcante', 'A148648-9', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (886, 'Adriany de Holanda Vieira', '118904531', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (887, 'Alyne Martins dos Santos', '2440822', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (888, 'Luenna Ingredy Lessa da Silva', '2335581', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (889, 'André Luiz Moura Reatequim', '114303371', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (890, 'Evandro Henrique da Silva', 'A855219', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (891, 'Wyllamys Cordeiro do Nascimento', '105503487', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (892, 'Ana Clara Gurgel Queirós', '2732157', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (893, 'Sidfran Camargo Nunes', '2064138', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (894, 'Avertânio Marques Medeiros', '2489856', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (895, 'Jean Faria dos Santos', 'A28580-3', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (896, 'Marcos Afonso Souza Nery', 'A1418998', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (897, 'Romário Guedes do Nascimento', 'A1247468', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (898, 'Izabele Santos da Silva', 'A2502879', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (899, 'Felício Barbosa da Silva', '2185040', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (900, 'Attilio Francesco Rondinelli', 'A1002325', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (901, 'Roberto Batista de Sousa Neto', '118578618', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (902, 'Leonardo Leite Drago', 'A1463730', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (903, 'Vanessa da Costa Ferreira', '1992570', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (904, 'Rogério Joza da Trindade', '2319590854', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (905, 'Edcelio da Silva Firmino', '706648943', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (906, 'Arnaldo Evangelista da Luz Júnior', '109217250', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (907, 'Márcio Costa', '119803950', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (908, 'Bruno Luiz de Vasconcelos Taveira', '1717091555', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (909, 'Carolina Costa de Siqueira', '2521369', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (910, 'Adriane Aparecida da Silva Cardoso Freitas', 'A1924729', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (911, 'Ever Richard da Silva Castro', 'A1924486', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (912, 'Flávio Kelner', 'A155098', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (913, 'Luanna Pegoretti Xavier', '117520314', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (914, 'Ana Carla Costa Magalhães Bonazoni', '119513072', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (915, 'Erisvan Freitas de Sousa', '119311011', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (916, 'José Clemildo Bezerra de Aquino', '415719321', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (917, 'Francisco das Chagas Souza de Nazaré', '120051508', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (918, 'Saymon Sombra Sampaio', 'A1838580', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (919, 'Alekchandra Karollynna Nunes de Lima Cameli', 'A1964399', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (920, 'Kéthlen Taynara Buzzo Feitosa', '1716199042', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (921, 'Airton de Oliveria Carvalho', '113208685', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (922, 'Cryspiniano Saraiva Machado', 'A1637681', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (923, 'Evilândia de Lima Silva', 'A1866206', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (924, 'Marcelo Silva Fonseca', 'A1663992', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (925, 'Gabriel Lucas Camêlo da Silva', '2783134', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (926, 'Freed Willian de Freitas Santos', '119922142', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (927, 'Gustavo de Holanda Souza', 'A2446014', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (928, 'João Vitor Gomes da Silva', '119915022', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (929, 'Guilherme Valente Gondin', 'A2514931', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (930, 'Wennedy da Silva Filgueira', 'A1285556', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (931, 'Hercy Martins Costa', '2603178750', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (932, 'Ricardo de Barros Curado', '2614724114', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (933, 'José Ennis Figueiredo Barbosa', '120059584', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (934, 'Kalyl Silva Leal', '2118397003', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (935, 'Anna Paula Costa de Sousa', 'A1847350', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (936, 'Kleyson Cella de Queiroz', '2319589384', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (937, 'Cilas Rodrigues da Silva', 'A1666398', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (938, 'Marilene Bezerra de Lima', '105793990', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (939, 'Vinícius Paiva de Mesquita', 'A2459795', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (940, 'João Wilson Felício Marques de Moraes', 'A1675729', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (941, 'Vanessa da Costa Ferreira Yunes', 'A2439875', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (942, 'Thiago Lima Santiago de Oliveira', '119973316', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (943, 'Francisco Maia de Andrade Neto', '408208295', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (944, 'Sidney Seixas Pereira de Lima Soares', 'A1491008', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (945, 'André Vitor de Almeida e Sousa', 'A1473590', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (946, 'Rayanne Rocha de Araújo', 'A1664158', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (947, 'Hanna Salim Paes Chaouk', '119943417', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (948, 'Flávio Luís Candian', '2606718038', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (950, 'Francisco Leônidas de Souza Pinheiro', '119969670', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (951, 'Natacha Salomão Chagas Almeida', 'A1358553', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (952, 'Matheus Fernandes da Costa', 'A1664263', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (953, 'Antônio Pericles de Miranda', '2310563790', NULL, '0', 4, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (954, 'Luís Eduardo dos Santos Magalhães', '119786850', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (955, 'Francisco Marcos Diniz Fernandes', '120271206', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (956, 'Paulo Ricardo do Nascimento de Góes', 'A2377420', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (957, 'Luis Alexandre Fustagno', '10058710', NULL, '0', 4, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (958, 'Mauro Ferreira Brasil', '1406105007', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (959, 'Gilberto Angelim das Chagas', 'A1835858', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (960, 'Eduarda de Freitas Pereira', '120189089', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (961, 'Kayo César Lopes Portilho', 'A1945564', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (962, 'Elpídio Rodrigues do Nascimento Júnior', 'A104715-9', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (963, 'Marcelo Almeida Farrapo', 'A1534122', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (964, 'Douglas Soares do Nascimento', '114330409', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (965, 'Thiago Silva dos Santos', '118550829', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (966, 'Maria Euridice Medeiros', 'A431141', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (967, 'Paulo Lopes da Silva Júnior', '119969122', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (968, 'Márcio Paulo Matos de Lima', 'A2530325', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (969, 'Isabelly Romero e Silva', 'A2480735', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (970, 'Hernane da Silveira Bandeira', 'A2394634', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (971, 'Jorge Luís da Silva Santos', 'A2524724', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (972, 'Neire Andrade de Araújo', '113207719', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (973, 'Sângelo Mota do Nascimento', '120309270', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (974, 'Reziery Lopes Lima', 'A2231280', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (975, 'Tatiany Bernardino', 'A1935704', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (976, 'Emily Miranda Sória', '119983427', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (977, 'Patrícia Messias de Carvalho', 'A1089331', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (978, 'Acelino Rodrigues Alves', '1500424773', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (979, 'Tatiana Silva de Souza Carneiro', 'A1246577', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (980, 'Rúryk Francisco Vilas Bôas Alves', '120815311', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (981, 'Isabelle Moreira Santiago', '117480622', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (982, 'Gabriel Abrahim Pinheiro', 'A1048341', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (983, 'Marjorie Silva Pacheco', 'A1852930', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (984, 'Francisco Alves Costa Júnior', '25533', NULL, 'AC', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (985, 'Allan Richard Damasceno', 'A1070606', NULL, 'AC', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (986, 'Ana Gabriella Valente de Moura', '22271 D', NULL, 'AC', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (987, 'Alane Moreira Vilacio', '00A2403501', NULL, 'AC', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (988, 'Diala Maria Ferreira Siqueira', '00A2634228', NULL, 'AC', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (989, 'Albeirton de Lucena Santiago', '00A1536230', NULL, 'AC', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (990, 'Patrícia Juliana Oliveira Fagundes', '00A2410800', NULL, 'AC', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (991, 'Rafaela Carlos Borges', '00A1121120', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (992, 'Francisco Oliveira de Sá', '00A1900196', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (993, 'Wendell Rosa Candido', '2609193335', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (994, 'Sarita Silva de Vila Feltrini', 'A50131-0', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (995, 'Thuãn Carlos da Silva Domingos', '118267477', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (996, 'Geraldo Fabricio Andreatto Fernandes', '119793431', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (997, 'Denilson Carvalho de Alencar', '119005042', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (998, 'Tatiele Rodrigues dos Santos', '00A2553112', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (999, 'Hyago Luís Sousa Pasquim', 'A2628643', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (1000, 'Frederico Lage de Loyola', '1405984074', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (1001, 'Airton Torres de Assis Souza', 'A2614391', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (1002, 'Aryane Guimarães Ribas Ottoni', 'A1728881', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (1003, 'Joseph da Silva Albuquerque', '120098490', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (1004, 'Thayrini Duanny Fernandes Bezerra', 'A1404245', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (1005, 'Adriane Brito do Nascimento', '118948610', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (1006, 'Ewerton Silva de Brito', '119230429', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (1007, 'Clauderson de Paula Sampaio', 'A1945521', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (1008, 'Marcos Vinicius Nascimento de Lima', 'A2672154', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (1009, 'Ivonilson da Costa Dias', 'A1869639', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (1010, 'Glaucon Rocha Dantas', '115027467', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (1011, 'Gabriel Tagliari Dourado', '21083098', NULL, '0', 1, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (1012, 'Nádja Natasha Araújo da Silva', 'A1247450', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (1013, 'Jussara Gonçalves Fernandes Rodrigues', 'A2746441', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (1014, 'Priscila Maria de Freitas Ribeiro', 'A1410733', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (1015, 'Amanda Guedes do Nascimento', 'A1837516', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (1016, 'Beatriz Santiago Wilke', 'A2554763', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (1017, 'Thawany Jhullary Araújo de Lima', 'A2618893', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (1018, 'Alan Vítor da Silva Souza', 'A2653591', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (1019, 'Luiz Felipe Aragão Werklaenhg', 'A1708600', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (1020, 'Artur Mendes Cardoso', 'A2452570', NULL, '0', 2, 1, NULL, NULL);
INSERT INTO responsaveltecnicoseker(IdResponsavelTecnico, Nome, CREA, Especializacao, UF, FK_especializacaoID, Orgao,
                                    FIELD8, FIELD9)
VALUES (1021, 'Thaís Yule Cabral de Souza', '118539329', NULL, '0', 1, 1, NULL, NULL);
