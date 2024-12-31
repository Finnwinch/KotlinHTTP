import Pickling.HTTP.*
import com.sun.security.ntlm.Server
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import kotlin.test.assertEquals

class HttpTest {

    private val urlGet : String = "https://jsonplaceholder.typicode.com/todos/1"
    private val urlPost : String = "https://jsonplaceholder.typicode.com/posts"
    private val utilisateurPost : Utilisateur = Utilisateur(userId = 1, title = "test", body = "salut")

    @Test fun httpTestGet() {
        Http(
            protocole = Protocole.GET,
            url = urlGet,
            header = mapOf("Content-Type" to "application/json")
        ).envoyer().après {
            if(réponse?.code!=200) fail("La requête HTTP aurais du réussire")
            if (extraireJson(réponse?.message as String) == null) fail("La requête HTTP aurais du retoruner un json sans typage")
        }
    }
    @Test fun httpTestPost() {
        Http(
            protocole = Protocole.POST,
            url = urlPost,
            header = mapOf("Content-Type" to "application/json"),
            body = utilisateurPost
        ).envoyer().après {
            if(réponse?.code!=201) fail("la ressource aurais du être crée")
            réponse?.message?.let { extraireJson(it) }?.let {
                val résultat:Utilisateur = Désérialisation(it)
                if (résultat.id != utilisateurPost.id &&
                    résultat.title != utilisateurPost.title &&
                    résultat.body != utilisateurPost.body &&
                    résultat.id != null) {
                    fail("la ressource aurais du renvoyer la même ressource crée avec un id")
                }
            }
        }
    }
}