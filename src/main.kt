import java.net.HttpURLConnection
import java.net.URI
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

data class GithubRepo(val name: String, val owner: Owner, val fork: Boolean)
data class Owner(val login: String)
data class GithubBranch(val name: String, val commit: Commit)
data class Commit(val sha: String)
data class ErrorResponse(val status: Int, val message: String)

fun fetchUrl(url: String): String {
    val conn = URI(url).toURL().openConnection() as HttpURLConnection
    conn.setRequestProperty("Accept", "application/vnd.github.v3+json")
    conn.connect()
    val code = conn.responseCode
    if (code == 404) throw NoSuchElementException("User not found")
    if (code != 200) throw RuntimeException("HTTP error: $code")
    return conn.inputStream.bufferedReader().readText()
}

fun main(args: Array<String>) {
    if (args.isEmpty()) {
        println(Gson().toJson(ErrorResponse(400, "Username required")))
        return
    }
    val username = args[0]
    val gson = Gson()
    try {
        val repoListType = object : TypeToken<List<GithubRepo>>() {}.type
        val repos: List<GithubRepo> = gson.fromJson(fetchUrl("https://api.github.com/users/$username/repos"), repoListType)
        val nonForks = repos.filter { !it.fork }

        val result = nonForks.map { repo ->
            val branchListType = object : TypeToken<List<GithubBranch>>() {}.type
            val branches: List<GithubBranch> =
                gson.fromJson(fetchUrl("https://api.github.com/repos/${repo.owner.login}/${repo.name}/branches"), branchListType)
            mapOf(
                "repository" to repo.name,
                "owner" to repo.owner.login,
                "branches" to branches.map { mapOf("name" to it.name, "lastCommitSha" to it.commit.sha) }
            )
        }
        println(gson.toJson(result))
    } catch (e: NoSuchElementException) {
        println(gson.toJson(ErrorResponse(404, e.message ?: "User not found")))
    } catch (e: Exception) {
        println(gson.toJson(ErrorResponse(500, e.message ?: "Unexpected error")))
    }
}
