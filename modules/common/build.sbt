Common.moduleSettings("common")

// Add here the specific settings for this module

resolvers += Resolver.url("Objectify Play Repository", url("http://deadbolt.ws/releases/"))(Resolver.ivyStylePatterns)

libraryDependencies ++= Common.commonDependencies