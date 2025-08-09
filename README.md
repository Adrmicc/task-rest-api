
# GitHub Repo Lister (Kotlin)

This is a simple Kotlin program that lists all public GitHub repositories of a given user (except forks) and shows:
- repository name
- owner login
- for each branch: branch name and last commit SHA

If the user does not exist, the program prints:
```
{
	"status": 404,
	"message": "User not found"
}
```

## How to run

1. Compile:
```
kotlinc src/Main.kt -cp lib/gson-2.10.1.jar -include-runtime -d app.jar
```
2. Run:
```
java -cp "app.jar;lib/gson-2.10.1.jar" MainKt <github-username>
```

Example output:
```
Repository: sample-repo
Owner: octocat
Branches:
	- main (sha: 123abc...)
	- dev (sha: 456def...)
```

The program uses the official [GitHub REST API v3](https://developer.github.com/v3).