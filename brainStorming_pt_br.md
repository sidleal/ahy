#Brainstorming em pt-BR: Pendências, idéias, módulos, tarefas, etc.

# Pendências #

## Suporte a demais bancos: Postgres, SQLServer, HSQLDB, Oracle. ##
  * Criar a classe parser para cada um desses bancos. Baseando-se na atual para MySQL. Essa classe é responsável pela criação e atualização da base, o acesso normal já é feito com JPA/hibernate que é independente de banco.

## Administração do site (em JavaFX) ##
  * Usaremos como inspiração as áreas de administração dos CMS atuais, porém simplificaremos ao extremo, usabilidade máxima.

## Wizard de instalação ##
  * Na primeira execução (após copiar o .jar para a pasta deploy do jboss) o ahy deve mostrar um wizard de instalação e solicitação dos principais dados do site, como título, endereço, etc. Além dos dados de acesso ao banco de dados, esses últimos deverão ser gravados nas configurações do jboss (xml e properties).

## Envio de emails (javamail) (20%) ##
  * Terminar a estrutura padrão de envio de emails, que será utilizada por diversos addons, como o de autenticação simples que enviará um email para habilitação do usuário/senha, usar javamail com smtp.

## Interface Rica JavaFX - comunicação por webservice (10%) ##
  * Evoluir a interface JavaFX e a interface de comunicação dela com os EJBs (provavelmente por WebServices, SOAP ou REST ainda não definido).

## Autenticação básica md5 (80%). ##
  * Grava usuário e hash da senha (não lembro se era md5 ou sha1), essa parte estava ok, mas quebrou quando atualizei pro jsf 2.0, precisamos também fazer o cadastro de usuários.

## Addon Autenticação Google ##
  * Addon para autenticação usando a API do google.

## Addon Autenticação Facebook ##
  * Addon para autenticação usando a API do Facebook.

## Addon Loja Eletrônica ##
  * Começaremos com o básico, cadastro de produtos, listas de preço, promoções em destaque, integração com pagseguro/paypal/locaweb (talvez um addon distinto para cada forma de pagto).

## Addon Fórum ##
  * Usando a estrutura de conteúdo já criada, organizar em fóruns por assunto, cadastrar moderadores.

## Addon Menu (80%) ##
  * Finalizar o addon de construção do menu, sem menu secundário inicialmente.

## Addon Views ##
  * Relatórios e formas de visualização/classificação dos conteúdos.

## Addon News ##
  * Parecido com views, mas mais focado na listagem tipo jornal.

## Addon RSS ##
  * Feed de notícias ou blog entries.

## Addon Twitter ##
  * Integração com twitter (publicar lá e listar conteúdo de lá).

## Addon Facebook ##
  * Integração com facebook (publicar lá e listar conteúdo de lá).

## Addon Picasa ##
  * Subir e mostrar fotos, agrupar por álbuns, permitir controle pelo nome do álbum.

## Addon Flickr ##
  * Subir e mostrar fotos, agrupar por álbuns, permitir controle pelo nome do álbum.

## Addon Youtube ##
  * Buscar vídeos, permitir filtros e algum controle pelo nome.

## Addon GoogleMaps ##
  * Integração com googlemaps.


---


# Concluídas #

## Multiplos sites em uma mesmo jboss app server ##
  * Assim como os melhores CMS do mercado, precisamos permitir que em um mesmo servidor sejam hospedados diversos sites, isso de forma simples e fácil de manter e atualizar. O JBoss permite configurar virtual hosts independentes, a dificuldade aqui é decidir como tratar o acesso à base de dados e EJB's de cada site, sem conflitos.
OK - http://code.google.com/p/ahy/issues/detail?id=2&can=1