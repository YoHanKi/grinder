document.addEventListener('DOMContentLoaded', function() {
    // 백엔드에서 피드를 가져오는 XMLHttpRequest
    let xhr = new XMLHttpRequest(); // XMLHttpRequest 객체 생성
    xhr.open('GET', '/get-feed', true); // 요청을 초기화합니다.
    xhr.onload = function () {
        if (xhr.status >= 200 && xhr.status < 300) {
            // 요청이 성공적으로 완료되면 실행됩니다.
            document.getElementById('feedContainer').innerHTML = xhr.responseText; // 응답을 headerContainer에 삽입
            initPage(); // 페이지 초기화 함수 호출
        } else {
            // 서버에서 4xx, 5xx 응답을 반환하면 오류 처리를 합니다.
            console.error('The request failed!');
        }
    };
    xhr.onerror = function () {
        // 요청이 네트워크 문제로 실패했을 때 실행됩니다.
        console.error('The request failed due to a network error!');
    };
    xhr.send(); // 요청을 서버로 보냅니다.

    // 페이지 초기화 함수
    function initPage() {
        // 톱니바퀴 드롭다운 초기화
        initGearDropdown();

        // 피드 삭제 이벤트 설정
        initDeleteFeedEvent();

        // 댓글 작성 이벤트 설정
        initCommentEvent();

        // 댓글 삭제 이벤트 설정 함수
        initDeleteCommentEvent();
    }

    // 톱니바퀴 드롭다운 초기화
    function initGearDropdown() {
        const gearBtns = document.querySelectorAll('.feed_gear_btn');
        gearBtns.forEach(function(btn) {
            btn.addEventListener('click', function(event) {
                const dropdown = this.nextElementSibling;
                dropdown.classList.toggle('show');

                // 다른 dropdown이 열려있는 경우 닫음
                const allDropdowns = document.querySelectorAll('.feed_gear_dropdown');
                allDropdowns.forEach(function(dropdownItem) {
                    if (dropdownItem !== dropdown) {
                        dropdownItem.classList.remove('show');
                    }
                });

                // 화면 어느 곳이나 클릭할 때 드롭다운 닫기
                document.addEventListener('click', function(e) {
                    if (!dropdown.contains(e.target) && !btn.contains(e.target)) {
                        dropdown.classList.remove('show');
                    }
                });
            });
        });
    }

    // 피드 삭제 이벤트 설정
    function initDeleteFeedEvent() {
        document.addEventListener('click', async function(event) {
            const target = event.target;

            // 피드 삭제 버튼 클릭시
            if (target.classList.contains('feed_delete_btn')) {
                // 피드 ID 가져오기
                const feedId = target.closest('.feed_container').querySelector('.feed_feed_id').value;

                // 확인 대화상자 표시
                if (confirm('삭제하시겠습니까?')) {
                    // 사용자가 확인을 선택한 경우 AJAX 요청을 통해 피드 삭제
                    await deleteFeed(feedId);
                    // 삭제 후 해당 피드를 화면에서 숨깁니다.
                    target.closest('.feed_container').style.display = 'none';
                }
            }
        });
    }

    // 댓글 작성 이벤트 설정
    function initCommentEvent() {
        document.addEventListener('click', async function(event) {
            const target = event.target;

            // 댓글 작성 버튼 클릭 시
            if (target.classList.contains('feed_comment_create_btn')) {
                const feedId = target.closest('.feed_container').querySelector('.feed_feed_id').value;
                let commentTextarea;
                let content;
                let parentCommentId = '';

                // 부모 댓글
                if (target.classList.contains('feed_parent_comment_create_btn')) {
                    commentTextarea = target.closest('.feed_parent_comment_write').querySelector('.feed_comment_textarea');
                    content = commentTextarea.value;
                }

                // 자식 댓글
                if (target.classList.contains('feed_child_comment_create_btn')) {
                    commentTextarea = target.closest('.feed_child_comment_write').querySelector('.feed_comment_textarea');
                    content = commentTextarea.value;
                    parentCommentId = target.closest('.feed_parent_comment_area').querySelector('.feed_parent_comment_id').value;
                }

                // 댓글 내용이 비어있는지 확인
                if (!content.trim()) {
                    alert('댓글 내용을 입력하세요.');
                    return;
                }

                await saveComment(content, parentCommentId, feedId);
                commentTextarea.value = '';
            }
        });
    }


    // 댓글 삭제 이벤트 설정 함수
    function initDeleteCommentEvent() {
        document.addEventListener('click', async function(event) {
            const target = event.target;

            // 댓글 삭제 버튼 클릭시
            if (target.classList.contains('feed_comment_delete_btn')) {
                let feedId = target.closest('.feed_container').querySelector('.feed_feed_id').value;
                let commentArea;
                let commentId;

                // 부모 댓글
                if (target.classList.contains('feed_parent_comment_delete_btn')) {
                    commentArea = target.closest('.feed_parent_comment_area');
                    commentId = commentArea.querySelector('.feed_parent_comment_id').value;
                }

                // 자식 댓글
                if (target.classList.contains('feed_child_comment_delete_btn')) {
                    commentArea = target.closest('.feed_child_comment_area');
                    commentId = commentArea.querySelector('.feed_child_comment_id').value;
                }

                await deleteComment(feedId, commentId);
                commentArea.style.display = 'none';
            }
        });
    }
});

// 피드 삭제 비동기 함수
async function deleteFeed(feedId) {
    try {
        const response = await fetch(`/feed/${feedId}`, {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json'
            }
        });

        if (response.ok) {
            const result = await response.json();
            console.log(result.message); // 성공 메시지 출력
            // 피드를 화면에서 제거하는 등의 추가적인 동작을 수행할 수 있습니다.
        } else {
            // 실패했을 때의 처리
            console.error('피드 삭제에 실패했습니다.');
        }
    } catch (error) {
        console.error('피드 삭제 중 오류가 발생했습니다:', error);
    }
}

// 댓글 저장 비동기 함수
async function saveComment(content, parentCommentId, feedId) {
    try {
        const response = await fetch(`/comment/${feedId}/newcomment`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                content: content,
                parentCommentId: parentCommentId
            })
        });

        if (response.ok) {
            const result = await response.json();
            console.log(result.message); // 성공 메시지 출력

            // 성공적으로 댓글이 저장된 후에 필요한 추가 작업 수행 가능
        } else {
            // 실패했을 때의 처리
            console.error('댓글 저장에 실패했습니다.');
        }
    } catch (error) {
        console.error('댓글 저장 중 오류가 발생했습니다:', error);
    }
}

// 댓글 삭제 비동기 함수
async function deleteComment(feedId, commentId) {
    try {
        const response = await fetch(`/comment/${feedId}/${commentId}`, {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json'
            }
        });

        if (response.ok) {
            const result = await response.json();
            console.log(result.message); // 성공 메시지 출력
            // 댓글 삭제 성공 시 추가 동작 수행 가능
        } else {
            // 실패했을 때의 처리
            console.error('댓글 삭제에 실패했습니다.');
        }
    } catch (error) {
        console.error('댓글 삭제 중 오류가 발생했습니다:', error);
    }
}