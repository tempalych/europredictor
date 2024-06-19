package com.tempalych.europredictor.service

import com.tempalych.europredictor.EuroPredictorApplication
import com.tempalych.europredictor.model.entity.Match
import com.tempalych.europredictor.model.entity.Team
import com.tempalych.europredictor.model.repository.ApiCallRepository
import com.tempalych.europredictor.model.repository.MatchRepository
import com.tempalych.europredictor.model.repository.TeamRepository
import org.junit.jupiter.api.Assertions
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.test.web.client.response.MockRestResponseCreators
import org.springframework.web.client.RestTemplate
import spock.lang.Specification

import java.time.Instant
import java.time.ZoneId
import java.time.temporal.ChronoUnit


@SpringBootTest
@ActiveProfiles(profiles = ["test"])
@ContextConfiguration(classes = [EuroPredictorApplication])
class FootballApiIntegrationServiceTests extends Specification {

    @Autowired
    FootballApiIntegrationService footballApiIntegrationService
    @Autowired
    TeamRepository teamRepository
    @Autowired
    MatchRepository matchRepository
    @Autowired
    ApiCallRepository apiCallRepository
    @Autowired
    AdminService adminService

    @Autowired
    RestTemplate restTemplate

    def setup() {
        createTeams()
        createMatches()
    }

    @DirtiesContext
    def "Updating results from API works correctly"() {
        given:
        def apiResponse = """
            {
              "response": [
                {
                  "teams": {
                    "home": {"id": 777, "name": "T\\u00fcrkiye", "winner": null},
                    "away": {"id": 1104, "name": "Georgia", "winner": null}
                  },
                  "goals": {"home": 5, "away": 5},
                  "score": {
                    "halftime": {"home": 1, "away": 1},
                    "fulltime": {"home": 5, "away": 5},
                    "extratime": {"home": null, "away": null},
                    "penalty": {"home": null, "away": null
                    }
                  }
                },
                {
                  "teams": {
                    "home": {"id": 27, "name": "Portugal", "winner": false},
                    "away": {"id": 770, "name": "Czech Republic", "winner": true}
                  },
                  "goals": {"home": 1, "away": 1},
                  "score": {
                    "halftime": {"home": 0, "away": 1},
                    "fulltime": {"home": 1, "away": 2},
                    "extratime": {"home": null, "away": null},
                    "penalty": {"home": null, "away": null}
                  }
                }
              ]
            }
            """
        def mockApiProviderServer = MockRestServiceServer.createServer(restTemplate)
        mockApiProviderServer.expect(
                request -> Assertions.assertEquals("/v3/fixtures", request.getURI().getPath()))
                .andRespond(MockRestResponseCreators.withSuccess(apiResponse, MediaType.APPLICATION_JSON))

        when: "One match result inserted manually"
        adminService.saveActualScore(1, 3, 3)

        and: "Second match result updated by API"
        footballApiIntegrationService.checkFinishedMatches()

        then:
        def matches = matchRepository.findAll()
        def matchUpdatedManually = matches.findAll {it.getId() == 1}.first
        def matchChanged = matches.findAll {it.getId() == 2}.first

        matchUpdatedManually.getHomeScore() == 3
        matchUpdatedManually.getVisitorScore() == 3

        matchChanged.getHomeScore() == 1
        matchChanged.getVisitorScore() == 2

        apiCallRepository.findAll().first().getCallCount() == 1
    }

    void createMatches() {
        def turkiye = teamRepository.findByName("Türkiye")
        def georgia = teamRepository.findByName("Georgia")
        def portugal = teamRepository.findByName("Portugal")
        def czechia = teamRepository.findByName("Czechia")

        def matchStartTime = Instant.now()
                .minus(110, ChronoUnit.MINUTES).atZone(ZoneId.of("UTC")).toLocalDateTime()
        def matches = [
                Match.builder().id(1).homeTeam(turkiye).visitorTeam(georgia).groupName("A")
                        .time(matchStartTime).build(),
                Match.builder().id(2).homeTeam(portugal).visitorTeam(czechia).groupName("A")
                        .time(matchStartTime).build(),
        ]
        matchRepository.saveAll(matches)
    }

    private void createTeams() {
        def teams = [
                Team.builder().name("Türkiye").groupName("A").flag("🇹🇷").build(),
                Team.builder().name("Georgia").groupName("A").flag("🇬🇪").build(),
                Team.builder().name("Portugal").groupName("A").flag("🇵🇹").build(),
                Team.builder().name("Czechia").groupName("A").flag("🇨🇿").build()
        ]
        teamRepository.saveAll(teams)
    }
}
