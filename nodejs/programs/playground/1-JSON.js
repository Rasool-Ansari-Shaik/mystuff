const book = {
    title: 'Ego is your enemy',
    author: 'Ryan Holiday'
}

console.log(book)

// Convert Java script object to JSON String
const bookJSON = JSON.stringify(book)
console.log(bookJSON)

// Parse JSON string and convert to JSON object
const bookParse = JSON.parse(bookJSON)
console.log(bookParse.title)
console.log(bookParse.author)
console.log(bookParse)

