import { rest } from 'msw'

/*
const authOwner = {
    "authority": "OWNER"
};

const userAdmin1 = {
    "id": 1,
    "username": "admin1",
    "authority": {
        "authority": "ADMIN"
    }
};

const userOwner1 = {
    "id": 2,
    "username": "owner1",
    "authority": authOwner
};

const userOwner2 = {
    "id": 3,
    "username": "owner2",
    "authority": authOwner
};

const userVet1 = {
    "id": 12,
    "username": "vet1",
    "authority": {
        "authority": "VET"
    }
};

const userVet2 = {
    "id": 13,
    "username": "vet2",
    "authority": {
        "authority": "VET"
    }
};

const owner1 = {
    "id": 1,
    "firstName": "George",
    "lastName": "Franklin",
    "address": "110 W. Liberty St.",
    "city": "Sevilla",
    "telephone": "608555103",
    "plan": "PLATINUM",
    "user": userOwner1
};

const owner2 = {
    "id": 2,
    "firstName": "Betty",
    "lastName": "Davis",
    "address": "638 Cardinal Ave.",
    "city": "Sevilla",
    "telephone": "608555174",
    "plan": "PLATINUM",
    "user": userOwner2
};

const pet1 = {
    "id": 1,
    "name": "Leo",
    "birthDate": "2010-09-07",
    "type": {
        "id": 1,
        "name": "cat"
    },
    "owner": owner1
};

const pet2 = {
    "id": 2,
    "name": "Basil",
    "birthDate": "2012-08-06",
    "type": {
        "id": 6,
        "name": "hamster"
    },
    "owner": owner2
};

const vet1 = {
    "id": 1,
    "firstName": "James",
    "lastName": "Carter",
    "specialties": [],
    "user": userVet1,
    "city": "Sevilla"
}

const vet2 = {
    "id": 2,
    "firstName": "Helen",
    "lastName": "Leary",
    "specialties": [
        {
            "id": 1,
            "name": "radiology"
        }
    ],
    "user": userVet2,
    "city": "Sevilla"
};

const visit1 = {
    "id": 1,
    "datetime": "2013-01-01T13:00:00",
    "description": "rabies shot",
    "pet": pet1,
    "vet": vet1,
    "city": "Badajoz",
};

const visit2 = {
    "id": 2,
    "datetime": "2013-01-02T15:30:00",
    "description": "",
    "pet": pet1,
    "vet": vet2
};

const consultation1 = {
    "id": 1,
    "title": "Mi gato no come",
    "status": "ANSWERED",
    "owner": owner2,
    "pet": pet1,
    "creationDate": "2023-04-11T11:20:00"
};

const consultation2 = {
    "id": 2,
    "title": "Título 2",
    "status": "PENDING",
    "owner": owner1,
    "pet": pet1,
    "creationDate": "2023-04-11T11:20:00"
};

const ticket1 = {
    "id": 1,
    "description": "What vaccine should my dog recieve?",
    "creationDate": "2023-01-04T17:32:00",
    "user": userOwner1,
    "consultation": consultation1
};

const ticket2 = {
    "id": 2,
    "description": "Rabies' one.",
    "creationDate": "2023-01-04T17:36:00",
    "user": userVet1,
    "consultation": consultation1
}
*/
const authAdmin = {
    "authority": "ADMIN"
};

const authPlayer = {
    "authority": "PLAYER"
};

const admin1 = {
    "id": 1,
    "username": "admin1",
    "authority": authAdmin
};

const admin2 = {
    "id": 5,
    "username": "admin2",
    "authority": authAdmin
};

const userPlayer1 = {
    "id": 2,
    "username": "player1",
    "authority": authPlayer
};

const userPlayer2 = {
    "id": 3,
    "username": "player2",
    "authority": authPlayer
};

const userPlayer3 = {
    "id": 4,
    "username": "player3",
    "authority": authPlayer
};

const userPlayer4 = {
    "id": 6,
    "username": "player4",
    "authority": authPlayer
};

const userPlayer5 = {
    "id": 7,
    "username": "player5",
    "authority": authPlayer
};

const player1 = {
    id: 1,
    first_name: 'Maria',
    last_name: 'Escobito',
    email: 'alvaro@gmail.com',
    birthday_date: '2003-01-04',
    registration_date: '2013-01-04',
    image: 'Estandar',
    user_id: 2
  };
  
  const player2 = {
    id: 2,
    first_name: 'David',
    last_name: 'Schroeder',
    email: 'alvaro1@gmail.com',
    birthday_date: '2003-01-04',
    registration_date: '2013-01-04',
    image: 'Estandar',
    user_id: 3
  };
  
  const player3 = {
    id: 3,
    first_name: 'Tete',
    last_name: 'Morente',
    email: 'alvaro2@gmail.com',
    birthday_date: '2003-01-04',
    registration_date: '2013-01-04',
    image: 'Estandar',
    user_id: 4
  };
  
  const player4 = {
    id: 4,
    first_name: 'Juanito',
    last_name: 'Afufue',
    email: 'alvaro3@gmail.com',
    birthday_date: '2003-01-04',
    registration_date: '2013-01-04',
    image: 'Estandar',
    user_id: 6
  };
  
  const player5 = {
    id: 5,
    first_name: 'Pepito',
    last_name: 'Palotes',
    email: 'alvaro4@gmail.com',
    birthday_date: '2003-01-04',
    registration_date: '2013-01-04',
    image: 'Estandar',
    user_id: 7
  };

  const game1 = {
    id: 2,
    code: '294R',
    created_at: '2011-01-04 17:29:12',
    started_at: '2013-01-04 17:30:12',
    finished_at: '2013-01-04 18:31:12',
    creator: 1,
    result: 'gano el player1'
  };
  
  const game2 = {
    id: 3,
    code: '3UG5',
    created_at: '2012-01-04 17:29:12',
    started_at: '2013-01-04 17:30:12',
    finished_at: '2013-01-04 17:31:12',
    creator: 1,
    result: 'gano el player2'
  };
  
  const gamePlayer1 = {
    game_id: 2,
    player_id: 1
  };
  
  const gamePlayer2 = {
    game_id: 2,
    player_id: 2
  };
  
  const gamePlayer3 = {
    game_id: 2,
    player_id: 3
  };
  
  const gamePlayer4 = {
    game_id: 2,
    player_id: 4
  };
  
  const gamePlayer5 = {
    game_id: 3,
    player_id: 1
  };
  
  const gamePlayer6 = {
    game_id: 3,
    player_id: 2
  };

  const friend1 = {
    id: 1,
    player1_id: 1,
    player2_id: 2,
    friend_request_status: true,
    invitation: null,
    invitation_as: null
  };
  
  const friend2 = {
    id: 2,
    player1_id: 1,
    player2_id: 3,
    friend_request_status: true,
    invitation: null,
    invitation_as: null
  };
  
  const friend3 = {
    id: 3,
    player1_id: 1,
    player2_id: 4,
    friend_request_status: true,
    invitation: null,
    invitation_as: null
  };
  
  const friend4 = {
    id: 4,
    player1_id: 2,
    player2_id: 3,
    friend_request_status: false,
    invitation: null,
    invitation_as: null
  };
  
  const friend5 = {
    id: 5,
    player1_id: 2,
    player2_id: 4,
    friend_request_status: true,
    invitation: null,
    invitation_as: null
  };

  const achievement1 = {
    id: 1,
    name: 'Nuevo pirata!!!',
    games_played: 0,
    games_won: 0
  };
  
  const achievement2 = {
    id: 2,
    name: 'Juega una partida',
    games_played: 1,
    games_won: 0
  };
  
  const achievement3 = {
    id: 3,
    name: 'Juega diez partidas',
    games_played: 10,
    games_won: 0
  };
  
  const achievement4 = {
    id: 4,
    name: 'Gana una partida',
    games_played: 1,
    games_won: 1
  };
  
  const achievement5 = {
    id: 5,
    name: 'Gana diez partidas',
    games_played: 10,
    games_won: 10
  };

  const playerAchievement1 = {
    achievement_id: 1,
    player_id: 1
  };
  
  const playerAchievement2 = {
    achievement_id: 1,
    player_id: 2
  };
  
  const playerAchievement3 = {
    achievement_id: 1,
    player_id: 3
  };
  
  const playerAchievement4 = {
    achievement_id: 1,
    player_id: 4
  };
  
  const playerAchievement5 = {
    achievement_id: 1,
    player_id: 5
  };
  
  const playerAchievement6 = {
    achievement_id: 2,
    player_id: 1
  };
  
  const playerAchievement7 = {
    achievement_id: 2,
    player_id: 2
  };
  
  const playerAchievement8 = {
    achievement_id: 2,
    player_id: 3
  };
  
  const playerAchievement9 = {
    achievement_id: 2,
    player_id: 4
  };
  
  const playerAchievement10 = {
    achievement_id: 2,
    player_id: 5
  };
  
  const playerAchievement11 = {
    achievement_id: 4,
    player_id: 1
  };
  
  const playerAchievement12 = {
    achievement_id: 4,
    player_id: 2
  };

export const handlers = [
    rest.delete('*/:id', (req, res, ctx) => {
        return res(
            ctx.status(200),
            ctx.json({
                message: "Entity deleted"
            }),
        )
    }),

    rest.get('*/api/v1/owners', (req, res, ctx) => {
        return res(
            ctx.status(200),
            ctx.json([
                owner1,
                owner2,
            ]),
        )
    }),

    rest.get('*/api/v1/pets', (req, res, ctx) => {
        return res(
            ctx.status(200),
            ctx.json([
                pet1,
                pet2,
            ]),
        )
    }),

    rest.get('*/api/v1/users', (req, res, ctx) => {
        return res(
            ctx.status(200),
            ctx.json([
                userAdmin1,
                userOwner1,
            ]),
        )
    }),

    rest.get('*/api/v1/vets', (req, res, ctx) => {
        return res(
            ctx.status(200),
            ctx.json([
                vet1,
                vet2,
            ]),
        )
    }),

    rest.get('*/api/v1/vets/specialties', (req, res, ctx) => {
        return res(
            ctx.status(200),
            ctx.json([
                {
                    "id": 1,
                    "name": "radiology"
                },
                {
                    "id": 2,
                    "name": "surgery"
                },
                {
                    "id": 3,
                    "name": "dentistry"
                }
            ]),
        )
    }),

    rest.get('*/api/v1/pets/:petId/visits', (req, res, ctx) => {
        return res(
            ctx.status(200),
            ctx.json([
                visit1,
                visit2,
            ]),
        )
    }),

    rest.get('*/api/v1/consultations', (req, res, ctx) => {
        return res(
            ctx.status(200),
            ctx.json([
                consultation1,
                consultation2,
            ]),
        )
    }),

    rest.get('*/api/v1/consultations/:id', (req, res, ctx) => {
        return res(
            ctx.status(200),
            ctx.json([
                consultation1,
            ]),
        )
    }),

    rest.get('*/api/v1/consultations/:id/tickets', (req, res, ctx) => {
        return res(
            ctx.status(200),
            ctx.json([
                ticket1,
                ticket2
            ]),
        )
    }),

    rest.post('*/api/v1/consultations/:id/tickets', (req, res, ctx) => {
        return res(
            ctx.status(200),
            ctx.json(
                {
                    "id": 3,
                    "description": "test ticket",
                    "creationDate": "2023-01-04T17:32:00",
                    "user": userOwner1,
                    "consultation": consultation1
                },
            ))
    }),

    rest.put('*/api/v1/consultations/:id', (req, res, ctx) => {
        return res(
            ctx.status(200),
            ctx.json(
                {
                    "id": 1,
                    "title": "Consulta sobre vacunas",
                    "status": "CLOSED",
                    "owner": owner1,
                    "pet": pet1,
                    "creationDate": "2023-01-04T17:30:00"
                }
            )
        )
    }),
    
]