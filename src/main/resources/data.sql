/*Authorities*/
INSERT INTO authorities(id, authority) VALUES (1,'ADMIN');
INSERT INTO authorities(id, authority) VALUES (2,'PLAYER');


/*2 Admins, contraseña:  4dm1n*/
INSERT INTO appusers(id, username, password, authority) VALUES (1,'admin1','$2a$10$nMmTWAhPTqXqLDJTag3prumFrAJpsYtroxf0ojesFYq0k4PmcbWUS',1);
INSERT INTO appusers(id, username, password, authority) VALUES (5,'admin2','$2a$10$nMmTWAhPTqXqLDJTag3prumFrAJpsYtroxf0ojesFYq0k4PmcbWUS',1);


/*Stats for players in the database*/
INSERT INTO stats(id, time_played, time_longest_game, time_shortest_game, games_played, games_won, average_score, average_cards_end_games) VALUES (1, '02:21:00', '01:01:00', '01:00:00', 2, 1, 47.5, 11.3);
INSERT INTO stats(id, time_played, time_longest_game, time_shortest_game, games_played, games_won, average_score, average_cards_end_games) VALUES (2, '04:01:00', '01:01:00', '01:00:00', 4, 1, 50.2, 12.1);
INSERT INTO stats(id, time_played, time_longest_game, time_shortest_game, games_played, games_won, average_score, average_cards_end_games) VALUES (3, '03:01:00', '01:01:00', '01:00:00', 3, 2, 56.8, 11.8);
INSERT INTO stats(id, time_played, time_longest_game, time_shortest_game, games_played, games_won, average_score, average_cards_end_games) VALUES (4, '02:01:00', '01:01:00', '01:00:00', 2, 0, 33.3, 10.3);
INSERT INTO stats(id, time_played, time_longest_game, time_shortest_game, games_played, games_won, average_score, average_cards_end_games) VALUES (5, '00:00:00', '00:00:00', '00:00:00', 0, 0, 0, 0);


/*5 Players, contraseña: 0wn3r*/
INSERT INTO appusers(id, username, password, authority) VALUES (2,'player1','$2a$10$DaS6KIEfF5CRTFrxIoGc7emY3BpZZ0.fVjwA3NiJ.BjpGNmocaS3e',2);
INSERT INTO appusers(id, username, password, authority) VALUES (3,'player2','$2a$10$DaS6KIEfF5CRTFrxIoGc7emY3BpZZ0.fVjwA3NiJ.BjpGNmocaS3e',2);
INSERT INTO appusers(id, username, password, authority) VALUES (4,'player3','$2a$10$DaS6KIEfF5CRTFrxIoGc7emY3BpZZ0.fVjwA3NiJ.BjpGNmocaS3e',2);
INSERT INTO appusers(id, username, password, authority) VALUES (6,'player4','$2a$10$DaS6KIEfF5CRTFrxIoGc7emY3BpZZ0.fVjwA3NiJ.BjpGNmocaS3e',2);
INSERT INTO appusers(id, username, password, authority) VALUES (7,'player5','$2a$10$DaS6KIEfF5CRTFrxIoGc7emY3BpZZ0.fVjwA3NiJ.BjpGNmocaS3e',2);

INSERT INTO players(id, first_name, last_name, email, birthday_date, registration_date, image, user_id, stats_id) VALUES (1, 'Maria', 'Escobito', 'alvaro@gmail.com', '2003-01-04', '2013-01-04','Estandar', 2, 1);
INSERT INTO players(id, first_name, last_name, email, birthday_date, registration_date, image, user_id, stats_id) VALUES (2, 'David', 'Schroeder', 'alvaro1@gmail.com','2003-01-04', '2013-01-04','foto2', 3, 2);
INSERT INTO players(id, first_name, last_name, email, birthday_date, registration_date, image, user_id, stats_id) VALUES (3, 'Tete', 'Morente', 'alvaro2@gmail.com','2003-01-04', '2013-01-04','foto3', 4, 3);
INSERT INTO players(id, first_name, last_name, email, birthday_date, registration_date, image, user_id, stats_id) VALUES (4, 'Juanito', 'Afufue', 'alvaro3@gmail.com','2003-01-04', '2013-01-04','foto4', 6, 4);
INSERT INTO players(id, first_name, last_name, email, birthday_date, registration_date, image, user_id, stats_id) VALUES (5, 'Pepito', 'Palotes', 'alvaro4@gmail.com','2003-01-04', '2013-01-04','foto5', 7, 5);


/*5 Games*/
INSERT INTO games(id, code, created_at, started_at, finished_at, creator) VALUES (1, '294R', '2013-01-04 17:29:12', '2013-01-04 17:30:12', '2013-01-04 18:31:12', 1); /*gano el player1*/
INSERT INTO games(id, code, created_at, started_at, finished_at, creator) VALUES (2, '3UG5', '2013-01-05 17:29:12', '2013-01-05 17:30:12', '2013-01-05 18:30:12', 1); /*gano el player2*/
INSERT INTO games(id, code, created_at, started_at, finished_at, creator) VALUES (3, '2UG5', '2014-01-04 17:29:12', '2014-01-04 17:30:12', '2014-01-04 18:30:12', 2); /*gano el player3*/
INSERT INTO games(id, code, created_at, started_at, finished_at, creator) VALUES (4, '8UG5', '2015-01-04 17:29:12', '2015-01-04 17:30:12', '2015-01-04 18:30:12', 2); /*gano el player3*/

/*Players playing the game*/
INSERT INTO game_players(game_id, player_id) VALUES (1,1);
INSERT INTO game_players(game_id, player_id) VALUES (1,2);
INSERT INTO game_players(game_id, player_id) VALUES (1,3);
INSERT INTO game_players(game_id, player_id) VALUES (1,4);
INSERT INTO game_players(game_id, player_id) VALUES (2,1);
INSERT INTO game_players(game_id, player_id) VALUES (2,2);
INSERT INTO game_players(game_id, player_id) VALUES (3,2);
INSERT INTO game_players(game_id, player_id) VALUES (3,3);
INSERT INTO game_players(game_id, player_id) VALUES (4,2);
INSERT INTO game_players(game_id, player_id) VALUES (4,3);
INSERT INTO game_players(game_id, player_id) VALUES (4,4);

/*Quitters of games*/
INSERT INTO game_quitters (game_id, quitter_id) VALUES (4,1);

/*Spectators of games*/
INSERT INTO game_spectators (game_id, spectator_id) VALUES (3,1);


/*Frienship relations*/
INSERT INTO friends(id, player1_id, player2_id, friend_request_status, invitation, invitation_as) VALUES (1,1,2, true, null, null);
INSERT INTO friends(id, player1_id, player2_id, friend_request_status, invitation, invitation_as) VALUES (2,1,3, true, null, null);
INSERT INTO friends(id, player1_id, player2_id, friend_request_status, invitation, invitation_as) VALUES (3,1,4, true, null, null);
INSERT INTO friends(id, player1_id, player2_id, friend_request_status, invitation, invitation_as) VALUES (4,2,3, false, null, null);
INSERT INTO friends(id, player1_id, player2_id, friend_request_status, invitation, invitation_as) VALUES (5,2,4, true, null, null);


/*Achievements*/
INSERT INTO achievements(id, name, games_played, games_won, dificulty) VALUES (1,'Nuevo pirata!!!',0,0,'Facil');
INSERT INTO achievements(id, name, games_played, games_won, dificulty) VALUES (2,'Juega una partida',1,0,'Medio');
INSERT INTO achievements(id, name, games_played, games_won, dificulty) VALUES (3,'Juega diez partidas',10,0,'Dificil');
INSERT INTO achievements(id, name, games_played, games_won, dificulty) VALUES (4,'Gana una partida',1,1,'Medio');
INSERT INTO achievements(id, name, games_played, games_won, dificulty) VALUES (5,'Gana diez partidas',10,10,'Dificil');

/*Achievements by players*/
INSERT INTO player_achievements(achievement_id, player_id) VALUES (1,1);
INSERT INTO player_achievements(achievement_id, player_id) VALUES (1,2);
INSERT INTO player_achievements(achievement_id, player_id) VALUES (1,3);
INSERT INTO player_achievements(achievement_id, player_id) VALUES (1,4);
INSERT INTO player_achievements(achievement_id, player_id) VALUES (1,5);
INSERT INTO player_achievements(achievement_id, player_id) VALUES (2,1);
INSERT INTO player_achievements(achievement_id, player_id) VALUES (2,2);
INSERT INTO player_achievements(achievement_id, player_id) VALUES (2,3);
INSERT INTO player_achievements(achievement_id, player_id) VALUES (2,4);
INSERT INTO player_achievements(achievement_id, player_id) VALUES (4,1);
INSERT INTO player_achievements(achievement_id, player_id) VALUES (4,2);
INSERT INTO player_achievements(achievement_id, player_id) VALUES (4,3);

