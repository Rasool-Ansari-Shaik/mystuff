function main(params) {
    console.log('executing hello function')
    msg = "Hello, " + params.name + " from " + params.place;
    console.log('Message: ', msg)
    return { greeting:  msg };
}