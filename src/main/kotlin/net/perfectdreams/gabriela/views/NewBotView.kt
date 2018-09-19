package net.perfectdreams.gabriela.views

import kotlinx.html.*
import net.perfectdreams.gabriela.utils.generateHeader
import org.jooby.Request
import org.jooby.Response

class NewBotView : BaseView() {
	override fun getContent(req: Request, res: Response): DIV.() -> Unit = {
		div {
			id = "content"
			div {
				id = "container"
				div {
					id = "text-container"
					generateHeader(
							"fas fa-plus",
							"Adicionar Bot",
							"Então quer dizer que você quer adicionar o seu bot na nossa lista? Então você veio ao lugar certo!"
					)
					p {
						+ "Para adicionar o seu bot em nossa lista, você terá que completar o nosso pequeno e simples formulário, nós recomendamos que você complete tudo que nós pedimos, já que as informações que você inserir serão analisadas pela nossa equipe!"
					}
					p {
						+ "Após você enviar o formulário, a nossa equipe irá verificar se o seu bot irá ser aprovado para ficar em nossa lista... ou não! Nós apenas adicionamos bots que nós analisamos e vemos que serão úteis para outras pessoas, antes de você adicionar, pergunte para você mesmo: \"O meu bot é útil para outras pessoas? Ele tem algo único que outros bots não tem?\", se você achar que sim, então vá em frente! \uD83D\uDE04"
					}
					p {
						+ "Lembrando que existem algumas pequenas regras e normas para bots que estão em nossa plataforma..."
					}
					ol {
						li {
							+ "Nós não iremos aceitar bots em que a maioria dos comandos são relacionados a conteúdo adulto."
						}
						li {
							+ "Você autoriza o PerfectDreams/VespertineDreams a hospedar, distribuir, licenciar, editar e monetizar o seu conteúdo sem nenhuma restrição."
						}
						li {
							+ "Nós temos o direito de aprovar ou rejeitar qualquer bot em qualquer momento, não importa qual o motivo."
						}
						li {
							+ "Seu bot deverá seguir todos os termos de uso do Discord."
						}
						li {
							+ "Você jamais deverá abusar (ou promover o abuso) de problemas em nossa plataforma para conseguir vantagens que outros bots não possuem."
						}
						li {
							+ "Seu bot não deverá enviar mensagens via mensagem direta para usuários que não solicitaram tais mensagens, por exemplo:"
							ul {
								li {
									+ "Possuir comandos que enviam mensagens diretas para usuários sem o usuário ter solicitado. (Como por exemplo, comandos de \"anunciar\")"
								}
								li {
									+ "Enviar mensagens diretas quando o usuário entrar no servidor ou realizar uma determinada ação. (Tudo bem, o seu bot pode ter tais funções, mas elas deverão ser opcionais e desativas por padrão, especialmente no VespertineDreams!)"
								}
							}
						}
						li {
							+ "Seu bot deverá apenas realizar ações quando o usuário pedir."
							ul {
								li {
									+ "Por exemplo, se o usuário usar \"-ping\" e o seu bot responder, tudo bem... mas se o usuário usar \"ping\" e o seu bot responder, aí já está passando dos limites."
								}
								li {
									+ "Seu bot pode ter tais funções, mas elas deverão ser opcionais e desativadas por padrão, especialmente no VespertineDreams!"
								}
							}
						}
						li {
							+ "Seu bot deverá ter funções únicas que outros bots desta lista não possuem."
						}
					}

					form(method = FormMethod.post) {
						input(name = "terms-of-use", type = InputType.checkBox) {
							+ " Eu aceito os termos de uso do VespertineDreams."
						}

						div(classes = "flavour-text") {
							+ "Client ID do Bot"
						}
						input(name = "client-botId", type = InputType.text) {
							style = "width: 100%"
						}
						div(classes = "flavour-text") { + "Prefixo do Bot" }
						input(name = "prefix", type = InputType.text) {
							style = "width: 100%"
						}
						div(classes = "flavour-text") { + "Qual é a linguagem de programação do seu bot?" }
						input(name = "programming-language", type = InputType.text) {
							style = "width: 100%"
						}
						div(classes = "flavour-text") { + "Porque o seu bot deve ser adicionado em nossa lista?" }
						textArea {
							name = "reason"
							style = "width: 100%"
						}
						button(type = ButtonType.submit) {
							+"Enviar"
						}
					}
				}
			}
		}
	}
}