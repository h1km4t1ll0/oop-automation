package ru.nsu.dolgov.taskchecker

Binding binding = new Binding([
        "config": config
])

/**
 * Function that make one big Groovy script and evaluates it.
 *
 * @return nothing.
 */
def load() {
    new GroovyShell(binding).evaluate(
            new File("./src/main/groovy/ru/nsu/dolgov/taskchecker/parse.groovy").text
                    + new File("./configuration/groups.groovy").text
                    + new File("./configuration/tasks.groovy").text
                    + new File("./src/main/groovy/ru/nsu/dolgov/taskchecker/dsl.groovy").text
                    + new File("./configuration/additional.groovy").text
    )
}

load()