package com.tempalych.europredictor.controller

import com.tempalych.europredictor.EuroPredictorApplication
import com.tempalych.europredictor.config.TestDataSourceConfig
import com.tempalych.europredictor.model.entity.Match
import com.tempalych.europredictor.model.entity.Team
import com.tempalych.europredictor.model.entity.User
import com.tempalych.europredictor.model.repository.MatchRepository
import com.tempalych.europredictor.model.repository.PredictionRepository
import com.tempalych.europredictor.model.repository.TeamRepository
import com.tempalych.europredictor.model.repository.UserRepository
import org.htmlunit.WebClient
import org.htmlunit.html.HtmlPage
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultMatcher
import org.springframework.web.context.WebApplicationContext
import spock.lang.Shared
import spock.lang.Specification

import java.time.LocalDateTime

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup

@SpringBootTest
@ActiveProfiles(profiles = ["test"])
@Import(TestDataSourceConfig)
@ContextConfiguration(classes = [EuroPredictorApplication])
class PredictionControllerTests extends Specification {

    @Shared
    MockMvc mockMvc

    @Autowired
    WebApplicationContext context
    @Autowired
    UserRepository userRepository
    @Autowired
    TeamRepository teamRepository
    @Autowired
    MatchRepository matchRepository
    @Autowired
    PredictionRepository predictionRepository

    def setup() {
        this.mockMvc = webAppContextSetup(this.context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build()

        createUsers()
        createTeams()
        createMatches()
    }

    def cleanup() {
        this.mockMvc = webAppContextSetup(this.context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build()

        predictionRepository.deleteAll()
        matchRepository.deleteAll()
        teamRepository.deleteAll()
        userRepository.deleteAll()
    }

    def "Unauthorized user can not get group list"() {
        when:
        def response = mockMvc.perform(get("/predictions")
                .with(httpBasic("some_unknown_user", "wrong_password")))
                .andReturn().response
        then:
        response.getStatus() == 401
    }

    def "Unauthorized user can not get list of matches"() {
        when:
        def response = mockMvc.perform(get("/group-matches?groupName=A")
                .with(httpBasic("some_unknown_user", "wrong_password")))
                .andReturn().response
        then:
        response.getStatus() == 401
    }

    def "Unauthorized user can not make predictions"() {
        when:
        def response = mockMvc.perform(post("/save-prediction-home?homeScore=1&visitorScore=&matchId=1")
                .accept("text/html-partial")
                .with(csrf())
                .with(httpBasic("some_unknown_user", "wrong_password")))
                .andReturn().response
        then:
        response.getStatus() == 401
    }

    def "Group list displays correctly for authorized user"() {
        when:
        def response =
                mockMvc.perform(get("/predictions")
                        .with(httpBasic("user1", "111")))
                        .andExpect(status().isOk())
                        .andReturn().response

        then:
        HtmlPage page
        try (WebClient webClient = new WebClient()) {
            page = webClient.loadHtmlCodeIntoCurrentWindow(response.getContentAsString())
        }

        def groupTabs = page.getElementsByName("grouptab")
        groupTabs.size() == 6
        groupTabs[0].getChildNodes()[0].toString() == "A 🇩🇪🏴󠁧󠁢󠁳󠁣󠁴󠁿 🇭🇺🇨🇭"
        groupTabs[1].getChildNodes()[0].toString() == "B 🇪🇸🇭🇷 🇮🇹🇦🇱"
        groupTabs[2].getChildNodes()[0].toString() == "C 🇸🇮🇩🇰 🇷🇸🏴󠁧󠁢󠁥󠁮󠁧󠁿"
        groupTabs[3].getChildNodes()[0].toString() == "D 🇵🇱🇳🇱 🇦🇹🇫🇷"
        groupTabs[4].getChildNodes()[0].toString() == "E 🇧🇪🇸🇰 🇷🇴🇺🇦"
        groupTabs[5].getChildNodes()[0].toString() == "F 🇹🇷🇬🇪 🇵🇹🇨🇿"
    }

    def "List of matches is correct for all groups"() {
        when:
        def response =
                mockMvc.perform(get("/group-matches?groupName=$group")
                        .with(httpBasic("user1", "111")))
                        .andExpect(status().isOk())
                        .andReturn().response
        then:
        HtmlPage page
        try (WebClient webClient = new WebClient()) {
            page = webClient.loadHtmlCodeIntoCurrentWindow(response.getContentAsString())
        }
        def matches = page.getElementsByName("match-info")
        matches.size() == 6
        page.getElementByName("match-info")
                .getByXPath("//form/span[@class='team-name-home']")
                .collect {it.asNormalizedText()} == homeTeams
        page.getElementByName("match-info")
                .getByXPath("//form/span[@class='team-name-visitor']")
                .collect {it.asNormalizedText()} == visitorTeams

        where:
        group || homeTeams                                                                | visitorTeams
        "A"   || ["Germany", "Hungary", "Germany", "Scotland", "Switzerland", "Scotland"] | ["Scotland", "Switzerland", "Hungary", "Switzerland", "Germany", "Hungary"]
        "B"   || ["Spain", "Italy", "Croatia", "Spain", "Croatia", "Albania"]             | ["Croatia", "Albania", "Albania", "Italy", "Italy", "Spain"]
        "C"   || ["Slovenia", "Serbia", "Slovenia", "Denmark", "England", "Denmark"]      | ["Denmark", "England", "Serbia", "England", "Slovenia", "Serbia"]
        "D"   || ["Poland", "Austria", "Poland", "Netherlands", "Netherlands", "France"]  | ["Netherlands", "France", "Austria", "France", "Austria", "Poland"]
        "E"   || ["Romania", "Belgium", "Slovakia", "Belgium", "Slovakia", "Ukraine"]     | ["Ukraine", "Slovakia", "Ukraine", "Romania", "Romania", "Belgium"]
        "F"   || ["Türkiye", "Portugal", "Georgia", "Türkiye", "Czechia", "Georgia"]      | ["Georgia", "Czechia", "Czechia", "Portugal", "Türkiye", "Portugal"]
    }

    def "Each user has his own predictions"() {
        setup:
        def matchId = matchRepository.findAll().stream()
                .filter { it.homeTeam.name == "Hungary" && it.visitorTeam.name == "Switzerland" }
                .toList().first()
                .getId()
        def user1Id = userRepository.findByUsername("user1").getId()
        def user2Id = userRepository.findByUsername("user2").getId()

        when: "First user updates the score of the 1st match. He sets 1:1"
        mockMvc.perform(post("/save-prediction-home?homeScore=1&visitorScore=&matchId=$matchId")
                .accept("text/html-partial")
                .with(csrf())
                .with(httpBasic("user1", "111")))
                .andReturn().response

        mockMvc.perform(post("/save-prediction-visitor?homeScore=1&visitorScore=1&matchId=$matchId")
                .accept("text/html-partial")
                .with(csrf())
                .with(httpBasic("user1", "111")))
                .andReturn().response

        and: "Second user updates the score of the 1st match. He sets 2:2"
        mockMvc.perform(post("/save-prediction-home?homeScore=2&visitorScore=&matchId=$matchId")
                .accept("text/html-partial")
                .with(csrf())
                .with(httpBasic("user2", "222")))
                .andReturn().response

        mockMvc.perform(post("/save-prediction-visitor?homeScore=2&visitorScore=2&matchId=$matchId")
                .accept("text/html-partial")
                .with(csrf())
                .with(httpBasic("user2", "222")))
                .andReturn().response

        and: "Each user requests new page"
        def responseUser1 =
                mockMvc.perform(get("/group-matches?groupName=A")
                        .with(httpBasic("user1", "111")))
                        .andExpect(status().isOk())
                        .andReturn().response

        def responseUser2 =
                mockMvc.perform(get("/group-matches?groupName=A")
                        .with(httpBasic("user2", "222")))
                        .andExpect(status().isOk())
                        .andReturn().response

        HtmlPage page1
        HtmlPage page2
        try (WebClient webClient = new WebClient()) {
            page1 = webClient.loadHtmlCodeIntoCurrentWindow(responseUser1.getContentAsString())
            page2 = webClient.loadHtmlCodeIntoCurrentWindow(responseUser2.getContentAsString())
        }

        then: "Match score is updated in DB"
        predictionRepository.findByUserIdAndMatchId(user1Id, matchId).homeScore == 1
        predictionRepository.findByUserIdAndMatchId(user1Id, matchId).visitorScore == 1
        predictionRepository.findByUserIdAndMatchId(user2Id, matchId).homeScore == 2
        predictionRepository.findByUserIdAndMatchId(user2Id, matchId).visitorScore == 2

        and: "Score on the returned page is updated"
        page1.getElementByName("match-info")
                .getByXPath("//form/input[@name='homeScore']")[1].asNormalizedText() == "1"
        page1.getElementByName("match-info")
                .getByXPath("//form/input[@name='visitorScore']")[1].asNormalizedText() == "1"

        page2.getElementByName("match-info")
                .getByXPath("//form/input[@name='homeScore']")[1].asNormalizedText() == "2"
        page2.getElementByName("match-info")
                .getByXPath("//form/input[@name='visitorScore']")[1].asNormalizedText() == "2"
    }

    private void createUsers() {
        def users = [
                User.builder()
                        .username("user1")
                        .password(new BCryptPasswordEncoder().encode("111"))
                        .build(),
                User.builder()
                        .username("user2")
                        .password(new BCryptPasswordEncoder().encode("222"))
                        .build()
                ]
        userRepository.saveAll(users)
    }

    private void createTeams() {
        def teams = [
                Team.builder().name("Germany").groupName("A").flag("🇩🇪").build(),
                Team.builder().name("Scotland").groupName("A").flag("🏴󠁧󠁢󠁳󠁣󠁴󠁿").build(),
                Team.builder().name("Hungary").groupName("A").flag("🇭🇺").build(),
                Team.builder().name("Switzerland").groupName("A").flag("🇨🇭").build(),

                Team.builder().name("Spain").groupName("B").flag("🇪🇸").build(),
                Team.builder().name("Croatia").groupName("B").flag("🇭🇷").build(),
                Team.builder().name("Italy").groupName("B").flag("🇮🇹").build(),
                Team.builder().name("Albania").groupName("B").flag("🇦🇱").build(),

                Team.builder().name("Slovenia").groupName("C").flag("🇸🇮").build(),
                Team.builder().name("Denmark").groupName("C").flag("🇩🇰").build(),
                Team.builder().name("Serbia").groupName("C").flag("🇷🇸").build(),
                Team.builder().name("England").groupName("C").flag("🏴󠁧󠁢󠁥󠁮󠁧󠁿").build(),

                Team.builder().name("Poland").groupName("D").flag("🇵🇱").build(),
                Team.builder().name("Netherlands").groupName("D").flag("🇳🇱").build(),
                Team.builder().name("Austria").groupName("D").flag("🇦🇹").build(),
                Team.builder().name("France").groupName("D").flag("🇫🇷").build(),

                Team.builder().name("Belgium").groupName("E").flag("🇧🇪").build(),
                Team.builder().name("Slovakia").groupName("E").flag("🇸🇰").build(),
                Team.builder().name("Romania").groupName("E").flag("🇷🇴").build(),
                Team.builder().name("Ukraine").groupName("E").flag("🇺🇦").build(),

                Team.builder().name("Türkiye").groupName("F").flag("🇹🇷").build(),
                Team.builder().name("Georgia").groupName("F").flag("🇬🇪").build(),
                Team.builder().name("Portugal").groupName("F").flag("🇵🇹").build(),
                Team.builder().name("Czechia").groupName("F").flag("🇨🇿").build()
        ]
        teamRepository.saveAll(teams)
    }

    void createMatches() {
        def germany = teamRepository.findByName("Germany")
        def scotland = teamRepository.findByName("Scotland")
        def hungary = teamRepository.findByName("Hungary")
        def switzerland = teamRepository.findByName("Switzerland")
        def spain = teamRepository.findByName("Spain")
        def croatia = teamRepository.findByName("Croatia")
        def italy = teamRepository.findByName("Italy")
        def albania = teamRepository.findByName("Albania")
        def slovenia = teamRepository.findByName("Slovenia")
        def denmark = teamRepository.findByName("Denmark")
        def serbia = teamRepository.findByName("Serbia")
        def england = teamRepository.findByName("England")
        def poland = teamRepository.findByName("Poland")
        def netherlands = teamRepository.findByName("Netherlands")
        def austria = teamRepository.findByName("Austria")
        def france = teamRepository.findByName("France")
        def belgium = teamRepository.findByName("Belgium")
        def slovakia = teamRepository.findByName("Slovakia")
        def romania = teamRepository.findByName("Romania")
        def ukraine = teamRepository.findByName("Ukraine")
        def turkiye = teamRepository.findByName("Türkiye")
        def georgia = teamRepository.findByName("Georgia")
        def portugal = teamRepository.findByName("Portugal")
        def czechia = teamRepository.findByName("Czechia")

        def matches = [
                Match.builder().homeTeam(germany).visitorTeam(scotland).groupName("A")
                        .time(LocalDateTime.now().plusMinutes(1)).build(), // this match will start in 1 minute

                Match.builder().homeTeam(hungary).visitorTeam(switzerland).groupName("A")
                        .time(LocalDateTime.now().plusDays(1)).build(),
                Match.builder().homeTeam(spain).visitorTeam(croatia).groupName("B")
                        .time(LocalDateTime.now().plusDays(2)).build(),
                Match.builder().homeTeam(italy).visitorTeam(albania).groupName("B")
                        .time(LocalDateTime.now().plusDays(3)).build(),

                Match.builder().homeTeam(poland).visitorTeam(netherlands).groupName("D")
                        .time(LocalDateTime.now().plusDays(4)).build(),
                Match.builder().homeTeam(slovenia).visitorTeam(denmark).groupName("C")
                        .time(LocalDateTime.now().plusDays(5)).build(),
                Match.builder().homeTeam(serbia).visitorTeam(england).groupName("C")
                        .time(LocalDateTime.now().plusDays(6)).build(),

                Match.builder().homeTeam(romania).visitorTeam(ukraine).groupName("E")
                        .time(LocalDateTime.now().plusDays(7)).build(),
                Match.builder().homeTeam(belgium).visitorTeam(slovakia).groupName("E")
                        .time(LocalDateTime.now().plusDays(8)).build(),
                Match.builder().homeTeam(austria).visitorTeam(france).groupName("D")
                        .time(LocalDateTime.now().plusDays(9)).build(),

                Match.builder().homeTeam(turkiye).visitorTeam(georgia).groupName("F")
                        .time(LocalDateTime.now().plusDays(10)).build(),
                Match.builder().homeTeam(portugal).visitorTeam(czechia).groupName("F")
                        .time(LocalDateTime.now().plusDays(11)).build(),

                Match.builder().homeTeam(croatia).visitorTeam(albania).groupName("B")
                        .time(LocalDateTime.now().plusDays(12)).build(),
                Match.builder().homeTeam(germany).visitorTeam(hungary).groupName("A")
                        .time(LocalDateTime.now().plusDays(13)).build(),
                Match.builder().homeTeam(scotland).visitorTeam(switzerland).groupName("A")
                        .time(LocalDateTime.now().plusDays(14)).build(),

                Match.builder().homeTeam(slovenia).visitorTeam(serbia).groupName("C")
                        .time(LocalDateTime.now().plusDays(15)).build(),
                Match.builder().homeTeam(denmark).visitorTeam(england).groupName("C")
                        .time(LocalDateTime.now().plusDays(16)).build(),
                Match.builder().homeTeam(spain).visitorTeam(italy).groupName("B")
                        .time(LocalDateTime.now().plusDays(17)).build(),

                Match.builder().homeTeam(slovakia).visitorTeam(ukraine).groupName("E")
                        .time(LocalDateTime.now().plusDays(18)).build(),
                Match.builder().homeTeam(poland).visitorTeam(austria).groupName("D")
                        .time(LocalDateTime.now().plusDays(19)).build(),
                Match.builder().homeTeam(netherlands).visitorTeam(france).groupName("D")
                        .time(LocalDateTime.now().plusDays(20)).build(),

                Match.builder().homeTeam(georgia).visitorTeam(czechia).groupName("F")
                        .time(LocalDateTime.now().plusDays(21)).build(),
                Match.builder().homeTeam(turkiye).visitorTeam(portugal).groupName("F")
                        .time(LocalDateTime.now().plusDays(22)).build(),
                Match.builder().homeTeam(belgium).visitorTeam(romania).groupName("E")
                        .time(LocalDateTime.now().plusDays(23)).build(),

                Match.builder().homeTeam(switzerland).visitorTeam(germany).groupName("A")
                        .time(LocalDateTime.now().plusDays(24)).build(),
                Match.builder().homeTeam(scotland).visitorTeam(hungary).groupName("A")
                        .time(LocalDateTime.now().plusDays(25)).build(),

                Match.builder().homeTeam(croatia).visitorTeam(italy).groupName("B")
                        .time(LocalDateTime.now().plusDays(26)).build(),
                Match.builder().homeTeam(albania).visitorTeam(spain).groupName("B")
                        .time(LocalDateTime.now().plusDays(27)).build(),

                Match.builder().homeTeam(netherlands).visitorTeam(austria).groupName("D")
                        .time(LocalDateTime.now().plusDays(28)).build(),
                Match.builder().homeTeam(france).visitorTeam(poland).groupName("D")
                        .time(LocalDateTime.now().plusDays(29)).build(),
                Match.builder().homeTeam(england).visitorTeam(slovenia).groupName("C")
                        .time(LocalDateTime.now().plusDays(30)).build(),
                Match.builder().homeTeam(denmark).visitorTeam(serbia).groupName("C")
                        .time(LocalDateTime.now().plusDays(31)).build(),

                Match.builder().homeTeam(slovakia).visitorTeam(romania).groupName("E")
                        .time(LocalDateTime.now().plusDays(32)).build(),
                Match.builder().homeTeam(ukraine).visitorTeam(belgium).groupName("E")
                        .time(LocalDateTime.now().plusDays(33)).build(),
                Match.builder().homeTeam(czechia).visitorTeam(turkiye).groupName("F")
                        .time(LocalDateTime.now().plusDays(34)).build(),
                Match.builder().homeTeam(georgia).visitorTeam(portugal).groupName("F")
                        .time(LocalDateTime.now().plusDays(35)).build()
        ]

        matchRepository.saveAll(matches)
    }

}
