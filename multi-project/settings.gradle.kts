rootProject.name = "sample-testing-multi-project"

include("module-a")
include("module-b")

includeBuild("../testing-plugin")
includeBuild("convenience-plugins")
