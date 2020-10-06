const yargs = require('yargs')

// yargs.version('1.1.0')

yargs.command({
    command: 'add',
    describe: 'Add a new note',
    builder: {
        title: {
            describe: 'Title Option',
            demandOption: true,
            type: 'string'
        },
        body: {
            describe: 'Body option',
            demandOption: true,
            type: String
        }
    },
    handler: function (argv) {
        console.log('Title: '+argv.title + '\n' + 'Body: '+argv.body)
    }
})

yargs.command({
    command: 'remove',
    describe: 'Remove a note',
    handler: function () {
        console.log('Removing a note')
    }
})

yargs.command({
    command: 'list',
    describe: 'List of notes',
    handler: function () {
        console.log('List of Notes!')
    }
})

yargs.command({
    command: 'read',
    describe: 'Read a note',
    handler: function () {
        console.log('Reading a note')
    }
})

yargs.parse()

// console.log(yargs.argv)