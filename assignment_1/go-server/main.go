package main

import (
	"fmt"
	"io/ioutil"
	"math/rand"
	"net/http"
	"time"

	"github.com/gin-gonic/gin"
)

// album represents data about a record album.
type album struct {
	Image   []byte  `json:"image"`
	Profile profile `json:"profile"`
}

type profile struct {
	Title  string `json:"title"`
	Artist string `json:"artist"`
	Year   string `json:"year"`
}

var profile1 = profile{Title: "Blue Train", Artist: "John Coltrane", Year: "2023"}
var profile2 = profile{Title: "Jeru", Artist: "Gerry Mulligan", Year: "1998"}
var profile3 = profile{Title: "Sarah Vaughan and Clifford Brown", Artist: "Sarah Vaughan", Year: "1997"}

var image1 = []byte{10}
var image2 = []byte{20}
var image3 = []byte{30}

var album1 = album{Image: image1, Profile: profile1}
var album2 = album{Image: image2, Profile: profile2}
var album3 = album{Image: image3, Profile: profile3}

// albums slice to seed record album data.
var albums = map[string]album{"1": album1, "2": album2, "3": album3}

func main() {
	router := gin.Default()
	router.GET("assignment_1_server_war/albums/:id", getAlbum)
	router.POST("assignment_1_server_war/albums", postAlbums)

	router.Run("localhost:8080")
}

func getAlbum(c *gin.Context) {
	id := c.Param("id")
	album, ok := albums[id]
	if !ok {
		c.JSON(http.StatusNotFound, gin.H{"error": fmt.Sprintf("Album with ID %s not found", id)})
		return
	}
	c.JSON(http.StatusOK, album.Profile)
}

func postAlbums(c *gin.Context) {
	// Parse the incoming form file upload
	file, err := c.FormFile("image")
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "Failed to get file"})
		return
	}

	// Open the uploaded file
	src, err := file.Open()
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Failed to open file"})
		return
	}
	defer src.Close()
	// Read the file content to calculate its size
	fileBytes, err := ioutil.ReadAll(src)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Failed to read file"})
		return
	}

	// Generate a random ID for the album
	rand.Seed(time.Now().UnixNano())
	randomID := fmt.Sprintf("%d", rand.Intn(10000))

	// Return the random ID and file size in JSON format
	fileSize := len(fileBytes)
	c.JSON(http.StatusOK, gin.H{
		"albumID":  randomID,
		"fileSize": fileSize,
	})
}
