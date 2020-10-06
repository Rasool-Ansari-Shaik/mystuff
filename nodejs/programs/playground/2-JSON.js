const fs = require('fs')

// const book = {
//     title: 'Ego is your enemy',
//     author: 'Ryan Holiday'
// }

// const bookJSON = JSON.stringify(book)
// fs.writeFileSync('1-json.json', bookJSON)

// const bookBuffer = fs.readFileSync('1-json.json')
// const bookJSON = bookBuffer.toString()
// const book = JSON.parse(bookJSON)
// console.log(book.title)
// console.log(book.author)

const dataBuffer = fs.readFileSync('2-json.json')
const dataJSON = dataBuffer.toString()
console.log("Before: ",dataJSON)
const data = JSON.parse(dataJSON)

data.name = "Rasool"
data.age = data.age + 1

const updatedData = JSON.stringify(data)
console.log("After: ",updatedData)
fs.writeFileSync('2-json.json', updatedData)