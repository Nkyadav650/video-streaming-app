
import { useEffect, useState } from 'react'
import './App.css'

function App() {
  const [video, setVideo] = useState("9b3d7a51-2f2e-4e70-941e-395a3a8d9aa6")
  const [allVideos, setAllVideos] = useState([]);
  const url = `http://localhost:8080/api/v1/videos`
  useEffect(() => {
    fetch(url + "/get-all")
      .then((res) => res.json())         // Convert response to JSON
      .then((data) => {
        setAllVideos(data);
        console.log("Fetched data:", data);  // Log the actual data
      })
      .catch((err) => {
        console.error("Error fetching data:", err); // Handle errors
      });
  }, []);

  return (
    <>
      <div className='stream-video'>
        <video src={url + `/stream/range/${video}`} width="500" height="500" controls></video>
      </div>
      <div className='video-list'>
        {allVideos && allVideos.map((content) => {
          return (
            <button style={{cursor:'pointer'}} onClick={()=>{setVideo(content.videoId)}}>
              <div key={content.videoId} >
                <video src={url + `/stream/${content.videoId}`} width="400px" height="400px" />
                <h4>{content.videoTitle}</h4>
                <span>{content.description}</span>
              </div>
            </button>
          );
        })}

      </div>
    </>
  )
}

export default App
