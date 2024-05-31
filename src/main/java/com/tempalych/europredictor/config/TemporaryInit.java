package com.tempalych.europredictor.config;

import com.tempalych.europredictor.model.entity.Match;
import com.tempalych.europredictor.model.entity.Team;
import com.tempalych.europredictor.model.entity.User;
import com.tempalych.europredictor.model.entity.UserRole;
import com.tempalych.europredictor.model.repository.MatchRepository;
import com.tempalych.europredictor.model.repository.PredictionRepository;
import com.tempalych.europredictor.model.repository.TeamRepository;
import com.tempalych.europredictor.model.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


//TODO: move to tests
@Configuration
public class TemporaryInit {

    @Autowired
    UserRepository userRepository;

    @Autowired
    TeamRepository teamRepository;

    @Autowired
    MatchRepository matchRepository;

    @Autowired
    private PredictionRepository predictionRepository;

//    @PostConstruct
    private void init() {
        predictionRepository.deleteAll();
        matchRepository.deleteAll();
        teamRepository.deleteAll();
        userRepository.deleteAll();
        createUsers();
        createTeams();
        createMatches();
    }
    private void createUsers() {
        List<User> users = new ArrayList<>();
        var artem = User.builder()
                .id(1L)
                .username("artem")
                .password(new BCryptPasswordEncoder().encode("123"))
                .build();
        users.add(artem);

        var max = User.builder()
                .id(2L)
                .username("max")
                .password(new BCryptPasswordEncoder().encode("321"))
                .build();
        users.add(max);

        var admin = User.builder()
                .id(3L)
                .username("admin")
                .password(new BCryptPasswordEncoder().encode("a123"))
                .role(UserRole.ROLE_ADMIN)
                .build();
        users.add(admin);

        userRepository.saveAll(users);
    }

    private void createTeams() {
        List<Team> teams = new ArrayList<>(List.of(
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
        ));
        teamRepository.saveAll(teams);
    }

    void createMatches() {
        var germany = teamRepository.findByName("Germany");
        var scotland = teamRepository.findByName("Scotland");
        var hungary = teamRepository.findByName("Hungary");
        var switzerland = teamRepository.findByName("Switzerland");
        var spain = teamRepository.findByName("Spain");
        var croatia = teamRepository.findByName("Croatia");
        var italy = teamRepository.findByName("Italy");
        var albania = teamRepository.findByName("Albania");
        var slovenia = teamRepository.findByName("Slovenia");
        var denmark = teamRepository.findByName("Denmark");
        var serbia = teamRepository.findByName("Serbia");
        var england = teamRepository.findByName("England");
        var poland = teamRepository.findByName("Poland");
        var netherlands = teamRepository.findByName("Netherlands");
        var austria = teamRepository.findByName("Austria");
        var france = teamRepository.findByName("France");
        var belgium = teamRepository.findByName("Belgium");
        var slovakia = teamRepository.findByName("Slovakia");
        var romania = teamRepository.findByName("Romania");
        var ukraine = teamRepository.findByName("Ukraine");
        var turkiye = teamRepository.findByName("Türkiye");
        var georgia = teamRepository.findByName("Georgia");
        var portugal = teamRepository.findByName("Portugal");
        var czechia = teamRepository.findByName("Czechia");

        List<Match> matches = new ArrayList<>(List.of(
                Match.builder().homeTeam(germany).visitorTeam(scotland).groupName("A")
                        .time(LocalDateTime.of(2024, 6, 14, 21, 0)).build(),

                Match.builder().homeTeam(hungary).visitorTeam(switzerland).groupName("A")
                        .time(LocalDateTime.of(2024, 6, 15, 15, 0)).build(),
                Match.builder().homeTeam(spain).visitorTeam(croatia).groupName("B")
                        .time(LocalDateTime.of(2024, 6, 15, 18, 0)).build(),
                Match.builder().homeTeam(italy).visitorTeam(albania).groupName("B")
                        .time(LocalDateTime.of(2024, 6, 15, 21, 0)).build(),

                Match.builder().homeTeam(poland).visitorTeam(netherlands).groupName("D")
                        .time(LocalDateTime.of(2024, 6, 16, 15, 0)).build(),
                Match.builder().homeTeam(slovenia).visitorTeam(denmark).groupName("C")
                        .time(LocalDateTime.of(2024, 6, 16, 18, 0)).build(),
                Match.builder().homeTeam(serbia).visitorTeam(england).groupName("C")
                        .time(LocalDateTime.of(2024, 6, 16, 21, 0)).build(),

                Match.builder().homeTeam(romania).visitorTeam(ukraine).groupName("E")
                        .time(LocalDateTime.of(2024, 6, 17, 15, 0)).build(),
                Match.builder().homeTeam(belgium).visitorTeam(slovakia).groupName("E")
                        .time(LocalDateTime.of(2024, 6, 17, 18, 0)).build(),
                Match.builder().homeTeam(austria).visitorTeam(france).groupName("D")
                        .time(LocalDateTime.of(2024, 6, 17, 21, 0)).build(),

                Match.builder().homeTeam(turkiye).visitorTeam(georgia).groupName("F")
                        .time(LocalDateTime.of(2024, 6, 18, 18, 0)).build(),
                Match.builder().homeTeam(portugal).visitorTeam(czechia).groupName("F")
                        .time(LocalDateTime.of(2024, 6, 18, 21, 0)).build(),

                Match.builder().homeTeam(croatia).visitorTeam(albania).groupName("B")
                        .time(LocalDateTime.of(2024, 6, 19, 15, 0)).build(),
                Match.builder().homeTeam(germany).visitorTeam(hungary).groupName("A")
                        .time(LocalDateTime.of(2024, 6, 19, 18, 0)).build(),
                Match.builder().homeTeam(scotland).visitorTeam(switzerland).groupName("A")
                        .time(LocalDateTime.of(2024, 6, 19, 21, 0)).build(),

                Match.builder().homeTeam(slovenia).visitorTeam(serbia).groupName("C")
                        .time(LocalDateTime.of(2024, 6, 20, 15, 0)).build(),
                Match.builder().homeTeam(denmark).visitorTeam(england).groupName("C")
                        .time(LocalDateTime.of(2024, 6, 20, 18, 0)).build(),
                Match.builder().homeTeam(spain).visitorTeam(italy).groupName("B")
                        .time(LocalDateTime.of(2024, 6, 20, 21, 0)).build(),

                Match.builder().homeTeam(slovakia).visitorTeam(ukraine).groupName("E")
                        .time(LocalDateTime.of(2024, 6, 21, 15, 0)).build(),
                Match.builder().homeTeam(poland).visitorTeam(austria).groupName("D")
                        .time(LocalDateTime.of(2024, 6, 21, 18, 0)).build(),
                Match.builder().homeTeam(netherlands).visitorTeam(france).groupName("D")
                        .time(LocalDateTime.of(2024, 6, 21, 21, 0)).build(),

                Match.builder().homeTeam(georgia).visitorTeam(czechia).groupName("F")
                        .time(LocalDateTime.of(2024, 6, 22, 15, 0)).build(),
                Match.builder().homeTeam(turkiye).visitorTeam(portugal).groupName("F")
                        .time(LocalDateTime.of(2024, 6, 22, 18, 0)).build(),
                Match.builder().homeTeam(belgium).visitorTeam(romania).groupName("E")
                        .time(LocalDateTime.of(2024, 6, 22, 21, 0)).build(),

                Match.builder().homeTeam(switzerland).visitorTeam(germany).groupName("A")
                        .time(LocalDateTime.of(2024, 6, 23, 21, 0)).build(),
                Match.builder().homeTeam(scotland).visitorTeam(hungary).groupName("A")
                        .time(LocalDateTime.of(2024, 6, 23, 21, 0)).build(),

                Match.builder().homeTeam(croatia).visitorTeam(italy).groupName("B")
                        .time(LocalDateTime.of(2024, 6, 24, 21, 0)).build(),
                Match.builder().homeTeam(albania).visitorTeam(spain).groupName("B")
                        .time(LocalDateTime.of(2024, 6, 24, 21, 0)).build(),

                Match.builder().homeTeam(netherlands).visitorTeam(austria).groupName("D")
                        .time(LocalDateTime.of(2024, 6, 25, 18, 0)).build(),
                Match.builder().homeTeam(france).visitorTeam(poland).groupName("D")
                        .time(LocalDateTime.of(2024, 6, 25, 18, 0)).build(),
                Match.builder().homeTeam(england).visitorTeam(slovenia).groupName("C")
                        .time(LocalDateTime.of(2024, 6, 25, 21, 0)).build(),
                Match.builder().homeTeam(denmark).visitorTeam(serbia).groupName("C")
                        .time(LocalDateTime.of(2024, 6, 25, 21, 0)).build(),

                Match.builder().homeTeam(slovakia).visitorTeam(romania).groupName("E")
                        .time(LocalDateTime.of(2024, 6, 26, 18, 0)).build(),
                Match.builder().homeTeam(ukraine).visitorTeam(belgium).groupName("E")
                        .time(LocalDateTime.of(2024, 6, 26, 18, 0)).build(),
                Match.builder().homeTeam(czechia).visitorTeam(turkiye).groupName("F")
                        .time(LocalDateTime.of(2024, 6, 26, 21, 0)).build(),
                Match.builder().homeTeam(georgia).visitorTeam(portugal).groupName("F")
                        .time(LocalDateTime.of(2024, 6, 26, 21, 0)).build()
        ));

        matchRepository.saveAll(matches);
    }

}
